package cn.liulingfengyu.actuator.bind;

import cn.liulingfengyu.rabbitmq.config.RabbitMQConfig;
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

    @Value("${actuator.name}")
    private String actuatorName;

    @Bean
    public Queue actuatorQueue() {
        return new Queue(actuatorName, true);
    }

    @Bean
    public FanoutExchange actuatorExchange() {
        return new FanoutExchange(RabbitMQConfig.ACTUATOR_EXCHANGE_NAME);
    }

    @Bean
    public Binding actuatorBinding(@Qualifier("actuatorQueue") Queue queue, @Qualifier("actuatorExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

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
