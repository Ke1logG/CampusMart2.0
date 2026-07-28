package org.example.CampusMart.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.web.config.RabbitmqConfig;
import org.example.CampusMart.web.mapper.OrderMapper;
import org.example.CampusMart.web.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 订单超时取消的 RabbitMQ 死信队列消费者。
 *
 * 这是订单超时取消的<b>主流程</b>：订单创建 30 分钟后，延迟消息过期进入死信队列，
 * 由本消费者消费并取消订单。即使消息重复消费，由于会判断订单状态，也具有幂等性。
 */
@Slf4j
@Component
public class OrderTimeoutListener {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentService paymentService;

    @RabbitListener(queues = RabbitmqConfig.ORDER_DEAD_QUEUE)
    public void handleOrderTimeout(String orderId) {
        log.info("收到订单超时消息，orderId={}", orderId);
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                log.warn("订单不存在，orderId={}", orderId);
                return;
            }
            if (!"CREATED".equals(order.getStatus())) {
                log.info("订单状态不是 CREATED，无需取消，orderId={}，status={}", orderId, order.getStatus());
                return;
            }

            order.setStatus("CANCELLED");
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);
            // 同步关闭该订单下未支付的支付单，防止订单取消后仍被支付
            paymentService.closePendingPaymentByOrderId(Long.valueOf(orderId));
            log.info("订单已自动取消，orderId={}", orderId);
        } catch (Exception e) {
            // 捕获异常，避免抛出后触发框架重试或消息重新入队形成死循环
            log.error("处理订单超时消息异常，orderId={}", orderId, e);
        }
    }
}
