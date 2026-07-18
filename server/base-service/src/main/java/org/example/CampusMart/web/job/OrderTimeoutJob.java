package org.example.CampusMart.web.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.web.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 订单超时取消兜底补偿任务。
 *
 * 主流程：订单创建时会向 RabbitMQ 延迟队列发送一条 30 分钟后过期的消息，
 * 由 {@link org.example.CampusMart.web.listener.OrderTimeoutListener} 消费并取消订单。
 *
 * 本定时任务作为兜底补偿：每 5 分钟扫描一次，只处理那些超过支付超时时间 5 分钟以上、
 * 仍未被取消的订单（给 RabbitMQ 消费者预留 5 分钟的处理窗口）。
 */
@Slf4j
@Component
public class OrderTimeoutJob {

    @Autowired
    private OrderMapper orderMapper;

    // 兜底补偿间隔：5 分钟
    private static final long COMPENSATE_INTERVAL_MILLIS = 5 * 60 * 1000;

    @Scheduled(fixedDelay = COMPENSATE_INTERVAL_MILLIS)
    public void closeTimeoutOrders() {
        // 只处理已经超时 5 分钟以上的订单，避免与 RabbitMQ 消费者竞争
        Date compensateDeadline = new Date(System.currentTimeMillis() - COMPENSATE_INTERVAL_MILLIS);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, "CREATED")
                .lt(Order::getPayTimeout, compensateDeadline);
        List<Order> timeoutOrders = orderMapper.selectList(wrapper);
        if (timeoutOrders.isEmpty()) {
            return;
        }
        for (Order order : timeoutOrders) {
            order.setStatus("CANCELLED");
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);
        }
        log.info("兜底补偿自动关闭超时订单 {} 条", timeoutOrders.size());
    }
}
