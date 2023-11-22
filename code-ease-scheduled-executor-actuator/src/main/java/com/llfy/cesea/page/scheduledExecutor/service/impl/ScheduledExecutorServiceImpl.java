package com.llfy.cesea.page.scheduledExecutor.service.impl;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.config.redis.publish.ConstantConfiguration;
import com.llfy.cesea.core.config.scheduledExecutor.conf.BaseScheduledExecutorService;
import com.llfy.cesea.core.config.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.core.config.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.core.config.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.core.config.scheduledExecutor.enums.IncidentEnum;
import com.llfy.cesea.page.scheduledExecutor.service.IScheduledExecutorService;
import com.llfy.cesea.utils.RedisUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 任务管理实现类
 *
 * @author 刘凌枫羽工作室
 */
@Service
public class ScheduledExecutorServiceImpl implements IScheduledExecutorService {

    private final BaseScheduledExecutorService baseScheduledExecutorService;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisUtil redisUtil;

    private final String appName;

    public ScheduledExecutorServiceImpl(
            BaseScheduledExecutorService baseScheduledExecutorService,
            StringRedisTemplate stringRedisTemplate,
            RedisUtil redisUtil,
            @Value("${app.name}") String appName) {
        this.baseScheduledExecutorService = baseScheduledExecutorService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisUtil = redisUtil;
        this.appName = appName;
    }

    @SneakyThrows
    @Override
    public void startOnce(TaskDto taskDto) {
        //检查任务是否在执行中
        if (redisUtil.hExists(appName, taskDto.getTaskId())) {
            ScheduledFutureDto scheduledFutureDto = JSON.parseObject(redisUtil.hGet(appName, taskDto.getTaskId()).toString(), ScheduledFutureDto.class);
            if (!scheduledFutureDto.isCancelled() && !scheduledFutureDto.isDone()) {
                throw new Exception("当前任务正在执行中");
            }
        }
        baseScheduledExecutorService.startOnce(taskDto, 0);
    }

    @SneakyThrows
    @Override
    public void startLoop(TaskDto taskDto) {
        //检查任务是否在执行中
        if (redisUtil.hExists(appName, taskDto.getTaskId())) {
            ScheduledFutureDto scheduledFutureDto = JSON.parseObject(redisUtil.hGet(appName, taskDto.getTaskId()).toString(), ScheduledFutureDto.class);
            if (!scheduledFutureDto.isCancelled()) {
                throw new Exception("当前任务正在执行中");
            }
        }
        baseScheduledExecutorService.startLoop(taskDto, 0);
    }

    @Override
    public void stop(String taskId) {
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskId);
        messageDto.setIncident(IncidentEnum.STOP.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }

    @Override
    public void remove(String taskId) {
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskId);
        messageDto.setIncident(IncidentEnum.REMOVE.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }

    @Override
    public void updateStatus(String taskId) {
        MessageDto messageDto = new MessageDto();
        messageDto.setTaskId(taskId);
        messageDto.setIncident(IncidentEnum.UPDATE_STATUS.getCode());
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
    }
}
