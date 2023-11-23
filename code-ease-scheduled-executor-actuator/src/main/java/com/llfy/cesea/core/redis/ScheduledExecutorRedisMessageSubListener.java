package com.llfy.cesea.core.redis;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
import com.llfy.cesea.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.utils.RedisUtil;
import com.llfy.cesea.utils.RespJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订阅监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Slf4j
@Component
public class ScheduledExecutorRedisMessageSubListener implements MessageListener {

    private final BaseScheduledExecutorService baseScheduledExecutorService;

    private final String appName;

    private final RedisUtil redisUtil;

    public ScheduledExecutorRedisMessageSubListener(
            BaseScheduledExecutorService baseScheduledExecutorService,
            @Value("${app.name}") String appName,
            RedisUtil redisUtil) {
        this.baseScheduledExecutorService = baseScheduledExecutorService;
        this.appName = appName;
        this.redisUtil = redisUtil;
    }

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("scheduledExecutor消费消息->{}", message.toString());
        MessageDto messageDto = JSON.parseObject(message.toString(), MessageDto.class);
        //启动
        if (IncidentEnum.START.getCode().equals(messageDto.getIncident())) {
            if (appName.equals(messageDto.getAppName())) {
                List<TaskDto> taskDtoList = messageDto.getTaskDtoList();
                for (TaskDto taskDto : taskDtoList){
                    //检查任务是否在执行中
                    if (redisUtil.hExists(appName, taskDto.getTaskId())) {
                        ScheduledFutureDto scheduledFutureDto = JSON.parseObject(redisUtil.hGet(appName, taskDto.getTaskId()).toString(), ScheduledFutureDto.class);
                        if (!scheduledFutureDto.isPeriodic() && !scheduledFutureDto.isCancelled() && !scheduledFutureDto.isDone()){
                            baseScheduledExecutorService.sendMessage(IncidentEnum.ERROR.getCode(), RespJson.error("当前任务正在执行中"));
                            continue;
                        }
                        if (scheduledFutureDto.isPeriodic() && !scheduledFutureDto.isCancelled()) {
                            baseScheduledExecutorService.sendMessage(IncidentEnum.ERROR.getCode(), RespJson.error("当前任务正在执行中"));
                            continue;
                        }
                    }
                    if (!taskDto.isPeriodic()){
                        baseScheduledExecutorService.startOnce(taskDto, 0);
                    }else {
                        baseScheduledExecutorService.startLoop(taskDto, 0);
                    }
                }
            }
        }
        //停止
        if (IncidentEnum.STOP.getCode().equals(messageDto.getIncident())) {
            baseScheduledExecutorService.stop(messageDto.getTaskId(), false);
        }
        //删除
        if (IncidentEnum.REMOVE.getCode().equals(messageDto.getIncident())) {
            baseScheduledExecutorService.remove(messageDto.getTaskId(), true);
        }
        //更新状态
        if (IncidentEnum.UPDATE_STATUS.getCode().equals(messageDto.getIncident())) {
            baseScheduledExecutorService.updateStatus(messageDto.getTaskId());
        }
        //任务转移
        if (IncidentEnum.TRANSFER.getCode().equals(messageDto.getIncident())) {
            //校验消息目标是否是本机器
            if (appName.equals(messageDto.getAppName())) {
                //循环消息
                for (ScheduledFutureDto scheduledFutureDto : messageDto.getScheduledFutureDtoList()) {
                    baseScheduledExecutorService.restart(scheduledFutureDto);
                }
            }
        }
    }
}
