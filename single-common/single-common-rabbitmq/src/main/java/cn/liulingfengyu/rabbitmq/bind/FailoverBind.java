package cn.liulingfengyu.rabbitmq.bind;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 故障转移队列
 */
@Component
public class FailoverBind {
    public static final String FAILOVER_QUEUE_NAME = "failover_queue";
    public static final String FAILOVER_EXCHANGE_NAME = "failover_exchange";
    public static final String FAILOVER_ROUTING_KEY = "failover_routing_key";

    @Bean
    public Queue failoverQueue() {
        return new Queue(FAILOVER_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange failoverExchange() {
        return new DirectExchange(FAILOVER_EXCHANGE_NAME);
    }

    @Bean
    public Binding failoverBinding(@Qualifier("failoverQueue") Queue queue, @Qualifier("failoverExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(FAILOVER_ROUTING_KEY);
    }
}
