package cn.liulingfengyu.rabbitmq.bind;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 执行器消息队列
 */
@Configuration
public class CallbackBind {

    public static final String CALLBACK_QUEUE_NAME = "actuator_callback_queue";
    public static final String CALLBACK_EXCHANGE_NAME = "actuator_callback_exchange";
    public static final String CALLBACK_ROUTING_KEY = "actuator_callback_routing_key";

    @Bean
    public Queue callbackQueue() {
        return new Queue(CALLBACK_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange callbackExchange() {
        return new DirectExchange(CALLBACK_EXCHANGE_NAME);
    }

    @Bean
    public Binding callbackBinding(@Qualifier("callbackQueue") Queue queue, @Qualifier("callbackExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CALLBACK_ROUTING_KEY);
    }
}
