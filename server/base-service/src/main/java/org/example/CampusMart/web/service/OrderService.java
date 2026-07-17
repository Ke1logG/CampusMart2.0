package org.example.CampusMart.web.service;

import org.example.CampusMart.web.vo.OrderVo;

import java.util.List;

public interface OrderService {

    OrderVo createOrder(Long buyerId, Long goodId);

    boolean cancelOrder(Long userId, Long orderId);

    boolean confirmReceipt(Long userId, Long orderId);

    OrderVo getOrderDetail(Long userId, Long orderId);

    List<OrderVo> listBuyerOrders(Long userId);

    List<OrderVo> listSellerOrders(Long userId);
}
