package cn.liulingfengyu.rabbitmq.bind;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 执行器消息队列
 */
@Configuration
public class ActuatorBind {

    public static final String ACTUATOR_EXCHANGE_NAME = "actuator_exchange";
    public static final String ACTUATOR_CALLBACK_QUEUE_NAME = "actuator_callback_queue";
    public static final String ACTUATOR_CALLBACK_EXCHANGE_NAME = "actuator_callback_exchange";
    public static final String ACTUATOR_CALLBACK_ROUTING_KEY = "actuator_callback_routing_key";
    @Value("${app.name}")
    private String actuatorName;

    @Bean
    public Queue actuatorQueue() {
        return new Queue(actuatorName, true);
    }

    @Bean
    public FanoutExchange actuatorExchange() {
        return new FanoutExchange(ACTUATOR_EXCHANGE_NAME);
    }

    @Bean
    public Binding actuatorBinding(@Qualifier("actuatorQueue") Queue queue, @Qualifier("actuatorExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Queue actuatorCallbackQueue() {
        return new Queue(ACTUATOR_CALLBACK_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange actuatorCallbackExchange() {
        return new DirectExchange(ACTUATOR_CALLBACK_EXCHANGE_NAME);
    }

    @Bean
    public Binding actuatorCallbackBinding(@Qualifier("actuatorCallbackQueue") Queue queue, @Qualifier("actuatorCallbackExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ACTUATOR_CALLBACK_ROUTING_KEY);
    }
}
