package com.llfy.cesea.core.config.redis.publish;


import com.llfy.cesea.core.config.scheduledExecutor.conf.ScheduledExecutorRedisMessageSubListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 发布监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Configuration
public class RedisPubListenerConfig {

    public static final String ONMESSAGE = "ONMESSAGE";

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter scheduledExecutorListenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 可以添加多个 messageListener，配置不同的交换机
        container.addMessageListener(scheduledExecutorListenerAdapter, new PatternTopic(ConstantConfiguration.SCHEDULED_EXECUTOR));
        return container;
    }

    /**
     * 消息适配器
     *
     * @param receiver 接收者
     * @return {@link MessageListenerAdapter}
     */
    @Bean
    MessageListenerAdapter scheduledExecutorListenerAdapter(ScheduledExecutorRedisMessageSubListener receiver) {
        return new MessageListenerAdapter(receiver, ONMESSAGE);
    }
}
