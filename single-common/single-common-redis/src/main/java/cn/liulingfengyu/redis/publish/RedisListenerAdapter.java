package cn.liulingfengyu.redis.publish;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisListenerAdapter {

    /**
     * 消息适配器
     *
     * @param receiver 接收者
     * @return {@link MessageListenerAdapter}
     */
    @Bean
    MessageListenerAdapter listenerAdapter(MessageSubListener receiver) {
        return new MessageListenerAdapter(receiver);
    }
}
