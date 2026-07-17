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

@Slf4j
@Component
public class OrderTimeoutJob {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(fixedDelay = 60000)
    public void closeTimeoutOrders() {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, "CREATED")
                .lt(Order::getPayTimeout, new Date());
        List<Order> timeoutOrders = orderMapper.selectList(wrapper);
        if (timeoutOrders.isEmpty()) {
            return;
        }
        for (Order order : timeoutOrders) {
            order.setStatus("CANCELLED");
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);
        }
        log.info("自动关闭超时订单 {} 条", timeoutOrders.size());
    }
}
