package org.example.CampusMart.web.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.common.exception.CampusMartException;
import org.example.CampusMart.common.result.ResultCodeEnum;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.model.entity.Payment;
import org.example.CampusMart.web.config.AlipayProperties;
import org.example.CampusMart.web.mapper.OrderMapper;
import org.example.CampusMart.web.mapper.PaymentMapper;
import org.example.CampusMart.web.service.PaymentService;
import org.example.CampusMart.web.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayProperties alipayProperties;

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CLOSED = "CLOSED";

    private static final String ORDER_STATUS_PAID = "PAID";
    private static final String ORDER_STATUS_SETTLED = "SETTLED";

    @Override
    public Payment getByOrderId(Long orderId) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderID, orderId);
        return paymentMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment createPayment(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new CampusMartException(ResultCodeEnum.DATA_ERROR.getCode(), "订单不存在");
        }
        if (!order.getBuyerID().equals(userId)) {
            throw new CampusMartException(ResultCodeEnum.ILLEGAL_REQUEST.getCode(), "无权支付该订单");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new CampusMartException(ResultCodeEnum.DATA_ERROR.getCode(), "订单状态不正确");
        }

        Payment exist = getByOrderId(orderId);
        if (exist != null) {
            return exist;
        }

        Payment payment = new Payment();
        payment.setOrderID(orderId);
        payment.setUserID(userId);
        payment.setAmount(order.getAmount());
        payment.setStatus(STATUS_PENDING);
        payment.setExpireTime(calcExpireTime());
        payment.setCreateTime(new Date());
        payment.setUpdateTime(new Date());
        paymentMapper.insert(payment);
        return payment;
    }

    @Override
    public String getAlipayOrderString(Long paymentId, Long userId) throws Exception {
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new CampusMartException(ResultCodeEnum.DATA_ERROR.getCode(), "支付单不存在");
        }
        if (!payment.getUserID().equals(userId)) {
            throw new CampusMartException(ResultCodeEnum.ILLEGAL_REQUEST.getCode(), "无权操作该支付单");
        }
        if (!STATUS_PENDING.equals(payment.getStatus())) {
            throw new CampusMartException(ResultCodeEnum.DATA_ERROR.getCode(), "支付单状态不正确");
        }

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        request.setReturnUrl(alipayProperties.getReturnUrl());

        String outTradeNo = String.valueOf(payment.getPaymentID());
        String totalAmount = payment.getAmount().toString();
        String subject = "CampusMart-订单" + payment.getOrderID();

        request.setBizContent("{"
                + "\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
                + "}");

        return alipayClient.pageExecute(request).getBody();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlipayNotify(Map<String, String> params) throws Exception {
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayProperties.getPublicKey(),
                "UTF-8",
                "RSA2"
        );
        if (!signVerified) {
            log.warn("支付宝通知验签失败");
            return false;
        }

        String tradeStatus = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            return true;
        }

        Long paymentId = Long.valueOf(params.get("out_trade_no"));
        String alipayTradeNo = params.get("trade_no");

        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            log.warn("支付单不存在: {}", paymentId);
            return false;
        }
        if (STATUS_PAID.equals(payment.getStatus())) {
            return true;
        }

        payment.setStatus(STATUS_PAID);
        payment.setAlipayTradeNo(alipayTradeNo);
        payment.setPayTime(new Date());
        payment.setUpdateTime(new Date());
        paymentMapper.updateById(payment);

        Order order = orderMapper.selectById(payment.getOrderID());
        if (order != null) {
            order.setStatus(ORDER_STATUS_PAID);
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);

            walletService.handleOrderSettled(order.getSellerID(), order.getOrderID(), order.getAmount());
        }

        return true;
    }

    private Date calcExpireTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        return cal.getTime();
    }
}
