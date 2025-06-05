package cn.liulingfengyu.actuator.support;

import cn.hutool.core.util.StrUtil;
import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 订阅监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Slf4j
@Component
public class MessageSubListener {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MyScheduledExecutorService myScheduledExecutorService;

    @Value("${actuator.name}")
    private String actuatorName;

    // 策略接口
    private interface IncidentHandler {
        void handle(CallbackBo callbackBo);
    }

    // 策略实现类
    private final Map<String, IncidentHandler> handlers = Map.of(
            IncidentEnum.START.getCode(), this::handleStart,
            IncidentEnum.UPDATE.getCode(), this::handleUpdate,
            IncidentEnum.STOP.getCode(), this::handleStop,
            IncidentEnum.REMOVE.getCode(), this::handleRemove
    );

    @RabbitListener(queues = ActuatorBind.ACTUATOR_QUEUE)
    public void onMessage(CallbackBo callbackBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 任务执行器不匹配
        TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
        if (StrUtil.isNotBlank(taskInfoBo.getAppName()) && !actuatorName.equals(taskInfoBo.getAppName())) {
            channel.basicAck(deliveryTag, false);
            return;
        }
        // 消息幂等处理
        String redisKey = RedisConstant.CALLBACK_IDEMPOTENT.concat(callbackBo.getUuId());
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 1, TimeUnit.DAYS))) {
            channel.basicAck(deliveryTag, false);
            return;
        }
        try {
            handlers.get(callbackBo.getIncident()).handle(callbackBo);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        }
    }

    private void handleStart(CallbackBo callbackBo) {
        myScheduledExecutorService.startOnce(callbackBo);
    }

    private void handleUpdate(CallbackBo callbackBo) {
        myScheduledExecutorService.update(callbackBo);
    }

    private void handleStop(CallbackBo callbackBo) {
        myScheduledExecutorService.stop(callbackBo);
    }

    private void handleRemove(CallbackBo callbackBo) {
        myScheduledExecutorService.remove(callbackBo);
    }
}