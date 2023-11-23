package com.llfy.cesea.scheduledExecutor.service.runnable;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
import com.llfy.cesea.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.utils.RedisUtil;
import com.llfy.cesea.utils.RespJson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 基础执行函数
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
@Slf4j
@Component
public class BaseRunnable implements Runnable {

    private String appName;

    private RedisUtil redisUtil;

    private TaskDto taskDto;

    private BaseScheduledExecutorService baseScheduledExecutorService;

    public BaseRunnable() {
    }

    public BaseRunnable(
            String appName,
            TaskDto taskDto,
            RedisUtil redisUtil,
            BaseScheduledExecutorService baseScheduledExecutorService) {
        this.appName = appName;
        this.taskDto = taskDto;
        this.redisUtil = redisUtil;
        this.baseScheduledExecutorService = baseScheduledExecutorService;
    }

    @Override
    public void run() {
        //手动变更缓存池中任务完成状态（未完成）
        ScheduledFutureDto scheduledFutureDto = JSON.parseObject(redisUtil.hGet(appName, taskDto.getTaskId()).toString(), ScheduledFutureDto.class);
        if (scheduledFutureDto != null) {
            scheduledFutureDto.setDone(false);
            redisUtil.hPut(appName, taskDto.getTaskId(), JSON.toJSONString(scheduledFutureDto));
            log.info("执行任务id->{}", taskDto.getTaskId());
            baseScheduledExecutorService.sendMessage(IncidentEnum.CARRY_OUT.getCode(), RespJson.success(scheduledFutureDto.getTaskDto()));
            //手动变更缓存池中任务完成状态（已完成）
            scheduledFutureDto.setDone(true);
            if (scheduledFutureDto.isPeriodic()) {
                scheduledFutureDto.setNextExecutionTime(scheduledFutureDto.getNextExecutionTime() + taskDto.getPeriod());
            }
            redisUtil.hPut(appName, taskDto.getTaskId(), JSON.toJSONString(scheduledFutureDto));
        }
    }
}
