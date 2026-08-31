package org.example.CampusMart.web.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.model.entity.Payment;
import org.example.CampusMart.web.config.AlipayProperties;
import org.example.CampusMart.web.mapper.OrderMapper;
import org.example.CampusMart.web.mapper.PaymentMapper;
import org.example.CampusMart.web.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private WalletService walletService;

    @Mock
    private AlipayClient alipayClient;

    @Mock
    private AlipayProperties alipayProperties;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        when(alipayProperties.getPublicKey()).thenReturn("mock_public_key");
    }

    @Test
    void handleAlipayNotify_shouldReturnFalse_whenSignVerifyFails() throws Exception {
        Map<String, String> params = buildNotifyParams("1", "TRADE_SUCCESS", "2024_001");
        try (MockedStatic<AlipaySignature> mocked = mockStatic(AlipaySignature.class)) {
            mocked.when(() -> AlipaySignature.rsaCheckV1(anyMap(), anyString(), anyString(), anyString()))
                    .thenReturn(false);

            boolean result = paymentService.handleAlipayNotify(params);

            assertFalse(result);
            verify(paymentMapper, never()).updateById(any(Payment.class));
        }
    }

    @Test
    void handleAlipayNotify_shouldReturnTrue_whenTradeStatusNotFinal() throws Exception {
        Map<String, String> params = buildNotifyParams("1", "WAIT_BUYER_PAY", "2024_001");
        try (MockedStatic<AlipaySignature> mocked = mockStatic(AlipaySignature.class)) {
            mocked.when(() -> AlipaySignature.rsaCheckV1(anyMap(), anyString(), anyString(), anyString()))
                    .thenReturn(true);

            boolean result = paymentService.handleAlipayNotify(params);

            assertTrue(result);
            verify(paymentMapper, never()).selectById(any());
            verify(paymentMapper, never()).updateById(any(Payment.class));
        }
    }

    @Test
    void handleAlipayNotify_shouldReturnFalse_whenPaymentNotFound() throws Exception {
        Map<String, String> params = buildNotifyParams("1", "TRADE_SUCCESS", "2024_001");
        try (MockedStatic<AlipaySignature> mocked = mockStatic(AlipaySignature.class)) {
            mocked.when(() -> AlipaySignature.rsaCheckV1(anyMap(), anyString(), anyString(), anyString()))
                    .thenReturn(true);
            when(paymentMapper.selectById(1L)).thenReturn(null);

            boolean result = paymentService.handleAlipayNotify(params);

            assertFalse(result);
            verify(paymentMapper, never()).updateById(any(Payment.class));
        }
    }

    @Test
    void handleAlipayNotify_shouldReturnTrue_whenPaymentAlreadyPaid() throws Exception {
        Map<String, String> params = buildNotifyParams("1", "TRADE_SUCCESS", "2024_001");
        Payment payment = Payment.builder().paymentID(1L).orderID(100L).status("PAID").build();
        try (MockedStatic<AlipaySignature> mocked = mockStatic(AlipaySignature.class)) {
            mocked.when(() -> AlipaySignature.rsaCheckV1(anyMap(), anyString(), anyString(), anyString()))
                    .thenReturn(true);
            when(paymentMapper.selectById(1L)).thenReturn(payment);

            boolean result = paymentService.handleAlipayNotify(params);

            assertTrue(result);
            verify(paymentMapper, never()).updateById(any(Payment.class));
            verify(orderMapper, never()).selectById(any());
        }
    }

    @Test
    void handleAlipayNotify_shouldClosePayment_whenOrderNotCreated() throws Exception {
        Map<String, String> params = buildNotifyParams("1", "TRADE_SUCCESS", "2024_001");
        Payment payment = Payment.builder()
                .paymentID(1L)
                .orderID(100L)
                .status("PENDING")
                .amount(new BigDecimal("99.00"))
                .build();
        Order order = Order.builder().orderID(100L).status("CANCELLED").build();
        try (MockedStatic<AlipaySignature> mocked = mockStatic(AlipaySignature.class)) {
            mocked.when(() -> AlipaySignature.rsaCheckV1(anyMap(), anyString(), anyString(), anyString()))
                    .thenReturn(true);
            when(paymentMapper.selectById(1L)).thenReturn(payment);
            when(orderMapper.selectById(100L)).thenReturn(order);

            boolean result = paymentService.handleAlipayNotify(params);

            assertTrue(result);
            verify(paymentMapper, times(2)).updateById(any(Payment.class));
            verify(orderMapper, never()).updateById(any(Order.class));
        }
    }

    @Test
    void handleAlipayNotify_shouldUpdateOrderToPaid_whenSuccess() throws Exception {
        Map<String, String> params = buildNotifyParams("1", "TRADE_SUCCESS", "2024_001");
        Payment payment = Payment.builder()
                .paymentID(1L)
                .orderID(100L)
                .status("PENDING")
                .amount(new BigDecimal("99.00"))
                .build();
        Order order = Order.builder()
                .orderID(100L)
                .buyerID(10L)
                .sellerID(20L)
                .amount(new BigDecimal("99.00"))
                .status("CREATED")
                .build();
        try (MockedStatic<AlipaySignature> mocked = mockStatic(AlipaySignature.class)) {
            mocked.when(() -> AlipaySignature.rsaCheckV1(anyMap(), anyString(), anyString(), anyString()))
                    .thenReturn(true);
            when(paymentMapper.selectById(1L)).thenReturn(payment);
            when(orderMapper.selectById(100L)).thenReturn(order);

            boolean result = paymentService.handleAlipayNotify(params);

            assertTrue(result);
            verify(paymentMapper).updateById(argThat(p -> "PAID".equals(p.getStatus()) && "2024_001".equals(p.getAlipayTradeNo())));
            verify(orderMapper).updateById(argThat(o -> "PAID".equals(o.getStatus())));
        }
    }

    private Map<String, String> buildNotifyParams(String outTradeNo, String tradeStatus, String tradeNo) {
        Map<String, String> params = new HashMap<>();
        params.put("out_trade_no", outTradeNo);
        params.put("trade_status", tradeStatus);
        params.put("trade_no", tradeNo);
        return params;
    }
}
