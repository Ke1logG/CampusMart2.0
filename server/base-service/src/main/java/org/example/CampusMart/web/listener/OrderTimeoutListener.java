package org.example.CampusMart.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.web.config.RabbitmqConfig;
import org.example.CampusMart.web.mapper.OrderMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class OrderTimeoutListener {

    @Autowired
    private OrderMapper orderMapper;

    @RabbitListener(queues = RabbitmqConfig.ORDER_DEAD_QUEUE)
    public void handleOrderTimeout(Long orderId) {
        log.info("收到订单超时消息，orderId={}", orderId);

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
        log.info("订单已自动取消，orderId={}", orderId);
    }
}
