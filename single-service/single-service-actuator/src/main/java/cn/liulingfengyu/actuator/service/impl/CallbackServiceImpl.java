package cn.liulingfengyu.actuator.service.impl;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.handler.CallbackHandler;
import cn.liulingfengyu.rabbitmq.bind.CallbackBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 回调 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
@Slf4j
public class CallbackServiceImpl {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private List<CallbackHandler> handlers;

    @RabbitListener(queues = CallbackBind.CALLBACK_QUEUE_NAME)
    public void callback(CallbackBo callbackBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 消息幂等处理
        String redisKey = RedisConstant.CALLBACK_IDEMPOTENT.concat(callbackBo.getUuId());
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 1, TimeUnit.DAYS))) {
            channel.basicAck(deliveryTag, false);
            return;
        }
        try {
            TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
            String taskCode = taskInfoBo.getCode();
            // 查找匹配的处理器
            for (CallbackHandler handler : handlers) {
                if (handler.supports(taskCode)) {
                    handler.handle(callbackBo);
                    break;
                }
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("消息处理失败", e);
            channel.basicReject(deliveryTag, false);
        }
    }
}
