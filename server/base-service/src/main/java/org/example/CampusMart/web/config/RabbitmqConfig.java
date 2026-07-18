package org.example.CampusMart.web.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单超时取消的 RabbitMQ 延迟队列配置。
 *
 * 设计：订单创建时发送一条消息到 order.delay.queue，30 分钟后消息过期进入死信队列
 * order.dead.queue，由 OrderTimeoutListener 消费并取消订单。
 *
 * 这是订单超时取消的<b>主流程</b>，定时任务 OrderTimeoutJob 作为兜底补偿。
 */
@Configuration
public class RabbitmqConfig {

    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.key";

    public static final String ORDER_DEAD_EXCHANGE = "order.dead.exchange";
    public static final String ORDER_DEAD_QUEUE = "order.dead.queue";
    public static final String ORDER_DEAD_ROUTING_KEY = "order.dead.key";

    // 30 分钟，单位毫秒，与订单支付超时时间保持一致
    public static final long ORDER_PAY_TIMEOUT_MILLIS = 30 * 60 * 1000;

    @Bean("orderDelayExchange")
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE);
    }

    @Bean("orderDeadExchange")
    public DirectExchange orderDeadExchange() {
        return new DirectExchange(ORDER_DEAD_EXCHANGE);
    }

    @Bean("orderDelayQueue")
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", ORDER_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_DEAD_ROUTING_KEY);
        args.put("x-message-ttl", ORDER_PAY_TIMEOUT_MILLIS);
        return QueueBuilder.durable(ORDER_DELAY_QUEUE).withArguments(args).build();
    }

    @Bean("orderDeadQueue")
    public Queue orderDeadQueue() {
        return QueueBuilder.durable(ORDER_DEAD_QUEUE).build();
    }

    @Bean
    public Binding orderDelayBinding(@Qualifier("orderDelayQueue") Queue queue,
                                     @Qualifier("orderDelayExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding orderDeadBinding(@Qualifier("orderDeadQueue") Queue queue,
                                    @Qualifier("orderDeadExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_DEAD_ROUTING_KEY);
    }
}
