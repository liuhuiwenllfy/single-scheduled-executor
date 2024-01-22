package cn.liulingfengyu.redis.publish;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.redis.bo.RedisMessageBo;
import cn.liulingfengyu.redis.utils.RedisUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 订阅监听配置
 *
 * @author 30346
 */
@Slf4j
@Component
@Getter
@Setter
@Primary
public abstract class MessageSubListener implements MessageListener {

    private volatile String channel;

    private volatile String msg;

    @Autowired
    private RedisUtil redisUtil;

    public abstract void onMessage();

    @Override
    public synchronized void onMessage(Message message, byte[] bytes) {
        channel = new String(bytes);
        RedisMessageBo redisMessageBo = JSONUtil.toBean(message.toString(), RedisMessageBo.class);
        msg = redisMessageBo.getMessage();
        String key = channel.concat(redisMessageBo.getUuid());
        if (!redisUtil.hasKey(key)){
            redisUtil.setEx(key, redisMessageBo.getMessage(), 3600, TimeUnit.MILLISECONDS);
            onMessage();
        }
    }
}
