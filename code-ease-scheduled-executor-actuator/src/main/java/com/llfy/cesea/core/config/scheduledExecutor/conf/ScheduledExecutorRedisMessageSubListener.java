package com.llfy.cesea.core.config.scheduledExecutor.conf;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.config.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.core.config.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.core.config.scheduledExecutor.enums.IncidentEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

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

    public ScheduledExecutorRedisMessageSubListener(
            BaseScheduledExecutorService baseScheduledExecutorService,
            @Value("${app.name}") String appName) {
        this.baseScheduledExecutorService = baseScheduledExecutorService;
        this.appName = appName;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("scheduledExecutor消费消息->{}", message.toString());
        MessageDto messageDto = JSON.parseObject(message.toString(), MessageDto.class);
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
            if (appName.equals(messageDto.getAppName())) {
                for (ScheduledFutureDto scheduledFutureDto : messageDto.getScheduledFutureDtoList()) {
                    baseScheduledExecutorService.restart(scheduledFutureDto);
                }
            }
        }
    }
}
