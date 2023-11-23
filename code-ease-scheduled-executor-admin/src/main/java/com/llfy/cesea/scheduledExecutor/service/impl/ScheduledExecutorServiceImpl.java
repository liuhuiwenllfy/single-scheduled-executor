package com.llfy.cesea.scheduledExecutor.service.impl;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.ConstantConfiguration;
import com.llfy.cesea.scheduledExecutor.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.scheduledExecutor.service.IScheduledExecutorService;
import com.llfy.cesea.utils.RedisUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务管理实现类
 *
 * @author 刘凌枫羽工作室
 */
@Service
public class ScheduledExecutorServiceImpl implements IScheduledExecutorService {


    private final StringRedisTemplate stringRedisTemplate;

    private final RedisUtil redisUtil;

    private final String actuatorName;

    public ScheduledExecutorServiceImpl(
            StringRedisTemplate stringRedisTemplate,
            RedisUtil redisUtil,
            @Value("${actuator.name}") String actuatorName) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisUtil = redisUtil;
        this.actuatorName = actuatorName;
    }

    @SneakyThrows
    @Override
    public void start(TaskDto taskDto) {
        //随机获取执行器
        Set<String> appNames = redisUtil.hKeys(actuatorName).stream().map(Object::toString).collect(Collectors.toSet());
        Random random = new Random();
        String appName = appNames.toArray(new String[0])[random.nextInt(appNames.size())];
        //组装消息体
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskDto.getTaskId());
        messageDto.setAppName(appName);
        messageDto.setTaskDtoList(Collections.singletonList(taskDto));
        messageDto.setIncident(IncidentEnum.START.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }

    @Override
    public void stop(String taskId) {
        //组装消息体
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskId);
        messageDto.setIncident(IncidentEnum.STOP.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }

    @Override
    public void remove(String taskId) {
        //组装消息体
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskId);
        messageDto.setIncident(IncidentEnum.REMOVE.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }

    @Override
    public void updateStatus(String taskId) {
        //组装消息体
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskId);
        messageDto.setIncident(IncidentEnum.UPDATE_STATUS.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }
}
