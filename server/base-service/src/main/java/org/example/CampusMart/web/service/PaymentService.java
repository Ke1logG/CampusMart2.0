package org.example.CampusMart.web.service;

import org.example.CampusMart.model.entity.Payment;

import java.util.Map;

public interface PaymentService {

    Payment getByOrderId(Long orderId);

    Payment createPayment(Long orderId, Long userId);

    String getAlipayOrderString(Long paymentId, Long userId) throws Exception;

    boolean handleAlipayNotify(Map<String, String> params) throws Exception;

    /**
     * 关闭指定订单下仍处于 PENDING 状态的支付单。
     * 在订单超时取消或订单状态异常时调用，防止已取消订单被继续支付。
     */
    void closePendingPaymentByOrderId(Long orderId);
}
