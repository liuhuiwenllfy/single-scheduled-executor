package cn.liulingfengyu.rabbitmq.bind;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 记录日志
 */
@Component
public class SchedulingLogBind {
    public static final String SCHEDULING_LOG_QUEUE_NAME = "scheduling_log_queue";
    public static final String SCHEDULING_LOG_EXCHANGE_NAME = "scheduling_log_exchange";
    public static final String SCHEDULING_LOG_ROUTING_KEY = "scheduling_log_routing_key";

    @Bean
    public Queue schedulingLogQueue() {
        return new Queue(SCHEDULING_LOG_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange schedulingLogExchange() {
        return new DirectExchange(SCHEDULING_LOG_EXCHANGE_NAME);
    }

    @Bean
    public Binding schedulingLogBinding(@Qualifier("schedulingLogQueue") Queue queue, @Qualifier("schedulingLogExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SCHEDULING_LOG_ROUTING_KEY);
    }
}
