package cn.liulingfengyu.actuator.support;

import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.redis.constant.RedisConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 订阅监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Slf4j
@Component
@Primary
public class MessageSubListener {

    @Autowired
    private MyScheduledExecutorService myScheduledExecutorService;

    @Value("${actuator.name}")
    private String actuatorName;

    @Autowired
    private ITaskInfoService taskInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 策略接口
    private interface IncidentHandler {
        void handle(TaskInfoBo taskInfoBo, TaskInfo taskInfo);
    }

    // 策略实现类
    private final Map<String, IncidentHandler> handlers = Map.of(
            IncidentEnum.START.getCode(), this::handleStart,
            IncidentEnum.UPDATE.getCode(), this::handleUpdate,
            IncidentEnum.STOP.getCode(), this::handleStop,
            IncidentEnum.REMOVE.getCode(), this::handleRemove
    );

    @RabbitListener(queues = "${actuator.name}")
    public void onMessage(TaskInfoBo taskInfoBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = taskInfoBo.getId(); // 假设消息的唯一标识是 taskInfoBo 的 id

        try {
            // 检查消息是否已经被处理过
            Boolean isProcessed = redisTemplate.opsForValue().setIfAbsent(RedisConstant.MESSAGE_IDEMPOTENT.concat(messageId), "1", 1, TimeUnit.HOURS);
            if (Boolean.FALSE.equals(isProcessed)) {
                log.warn("Message already processed: {}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            if (actuatorName.equals(taskInfoBo.getAppName())) {
                // 获取任务信息
                TaskInfo taskInfo = getTaskInfo(taskInfoBo);

                // 根据事件类型调用对应的处理逻辑
                String incidentCode = taskInfoBo.getIncident();
                IncidentHandler handler = handlers.get(incidentCode);
                if (handler != null) {
                    handler.handle(taskInfoBo, taskInfo);
                } else {
                    log.warn("Unsupported incident type: {}", incidentCode);
                }
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        }
    }

    // 提取公共方法：创建 TaskInfo 对象并拷贝属性
    private TaskInfo getTaskInfo(TaskInfoBo taskInfoBo) {
        TaskInfo taskInfo = new TaskInfo();
        BeanUtils.copyProperties(taskInfoBo, taskInfo);
        taskInfo.setCreateTime(new Date());
        return taskInfo;
    }

    // 各事件类型的处理逻辑
    private void handleStart(TaskInfoBo taskInfoBo, TaskInfo taskInfo) {
        TaskInfo existingTask = taskInfoService.getById(taskInfoBo.getId());
        if (existingTask == null || existingTask.isCancelled()) {
            myScheduledExecutorService.startOnce(taskInfo, taskInfoBo.getIncident());
        }
    }

    private void handleUpdate(TaskInfoBo taskInfoBo, TaskInfo taskInfo) {
        myScheduledExecutorService.update(taskInfo);
    }

    private void handleStop(TaskInfoBo taskInfoBo, TaskInfo taskInfo) {
        myScheduledExecutorService.stop(taskInfo, false);
    }

    private void handleRemove(TaskInfoBo taskInfoBo, TaskInfo taskInfo) {
        myScheduledExecutorService.remove(taskInfo, true);
    }
}