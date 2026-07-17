package org.example.CampusMart.web.service;

import org.example.CampusMart.model.entity.Payment;

import java.util.Map;

public interface PaymentService {

    Payment getByOrderId(Long orderId);

    Payment createPayment(Long orderId, Long userId);

    String getAlipayOrderString(Long paymentId, Long userId) throws Exception;

    boolean handleAlipayNotify(Map<String, String> params) throws Exception;
}
