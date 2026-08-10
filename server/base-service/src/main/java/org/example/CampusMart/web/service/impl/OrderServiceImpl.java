package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.CampusMart.common.exception.CampusMartException;
import org.example.CampusMart.common.result.ResultCodeEnum;
import org.example.CampusMart.model.entity.Goods;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.model.entity.Picture;
import org.example.CampusMart.web.config.RabbitmqConfig;
import org.example.CampusMart.web.mapper.OrderMapper;
import org.example.CampusMart.web.mapper.PictureMapper;
import org.example.CampusMart.web.service.GoodsService;
import org.example.CampusMart.web.service.OrderService;
import org.example.CampusMart.web.service.PaymentService;
import org.example.CampusMart.web.service.WalletService;
import org.example.CampusMart.web.vo.OrderVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentService paymentService;

    private static final String STATUS_CREATED = "CREATED";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_SETTLED = "SETTLED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVo createOrder(Long buyerId, Long goodId) {
        Goods goods = goodsService.getById(goodId);
        if (goods == null) {
            throw new CampusMartException(ResultCodeEnum.GOODS_NOT_FOUND);
        }
        if (goods.getPublishUserID().equals(buyerId)) {
            throw new CampusMartException(ResultCodeEnum.GOODS_SELF_PURCHASE);
        }
        LambdaQueryWrapper<Order> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Order::getBuyerID, buyerId)
                    .eq(Order::getGoodID, goodId)
                    .eq(Order::getStatus, "CREATED");
        Order existingOrder = orderMapper.selectOne(existWrapper);
        if (existingOrder != null) {
            return convertToVo(existingOrder);
        }

        Order order = new Order();
        order.setGoodID(goodId);
        order.setBuyerID(buyerId);
        order.setSellerID(goods.getPublishUserID());
        order.setAmount(new BigDecimal(goods.getPrice()));
        order.setStatus(STATUS_CREATED);
        order.setPayTimeout(calcPayTimeout());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insert(order);

        // 发送 RabbitMQ 延迟消息，30 分钟后由死信队列消费者取消未支付订单
        rabbitTemplate.convertAndSend(
                RabbitmqConfig.ORDER_DELAY_EXCHANGE,
                RabbitmqConfig.ORDER_DELAY_ROUTING_KEY,
                order.getOrderID()
        );

        return convertToVo(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long userId, Long orderId) {
        Order order = getAndCheck(orderId, userId);
        if (!STATUS_CREATED.equals(order.getStatus())) {
            throw new CampusMartException(ResultCodeEnum.ORDER_CANCEL_NOT_ALLOWED);
        }
        order.setStatus(STATUS_CANCELLED);
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);
        paymentService.closePendingPaymentByOrderId(orderId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceipt(Long userId, Long orderId) {
        Order order = getAndCheck(orderId, userId);
        if (!STATUS_PAID.equals(order.getStatus())) {
            throw new CampusMartException(ResultCodeEnum.ORDER_CONFIRM_NOT_ALLOWED);
        }
        order.setStatus(STATUS_SETTLED);
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);

        walletService.handleOrderSettled(order.getSellerID(), order.getOrderID(), order.getAmount());
        return true;
    }

    @Override
    public OrderVo getOrderDetail(Long userId, Long orderId) {
        Order order = getAndCheck(orderId, userId);
        return convertToVo(order);
    }

    /*
     * 旧实现，存在 N+1 查询问题，保留用于对比
     */
//    @Override
//    public List<OrderVo> listBuyerOrders(Long userId) {
//        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Order::getBuyerID, userId).orderByDesc(Order::getCreateTime);
//        List<Order> list = orderMapper.selectList(wrapper);
//        return list.stream().map(this::convertToVo).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<OrderVo> listSellerOrders(Long userId) {
//        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Order::getSellerID, userId).orderByDesc(Order::getCreateTime);
//        List<Order> list = orderMapper.selectList(wrapper);
//        return list.stream().map(this::convertToVo).collect(Collectors.toList());
//    }

    @Override
    public List<OrderVo> listBuyerOrders(Long userId) {
        return orderMapper.selectBuyerOrderListWithGoods(userId);
    }

    @Override
    public List<OrderVo> listSellerOrders(Long userId) {
        return orderMapper.selectSellerOrderListWithGoods(userId);
    }

    private Order getAndCheck(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new CampusMartException(ResultCodeEnum.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerID().equals(userId) && !order.getSellerID().equals(userId)) {
            throw new CampusMartException(ResultCodeEnum.ORDER_NO_PERMISSION);
        }
        return order;
    }

    private OrderVo convertToVo(Order order) {
        OrderVo vo = new OrderVo();
        BeanUtils.copyProperties(order, vo);
        Goods goods = goodsService.getById(order.getGoodID());
        if (goods != null) {
            vo.setGoodTitle(goods.getTitle());
            LambdaQueryWrapper<Picture> pictureWrapper = new LambdaQueryWrapper<>();
            pictureWrapper.eq(Picture::getGoodID, goods.getGoodID()).orderByDesc(Picture::getPictureID).last("limit 1");
            Picture picture = pictureMapper.selectOne(pictureWrapper);
            if (picture != null) {
                vo.setGoodPictureURL(picture.getPictureURL());
            }
        }
        return vo;
    }

    private Date calcPayTimeout() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        return cal.getTime();
    }
}
