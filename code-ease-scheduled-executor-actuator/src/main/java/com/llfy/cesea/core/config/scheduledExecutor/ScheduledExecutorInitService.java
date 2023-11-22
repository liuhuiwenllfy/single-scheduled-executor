package com.llfy.cesea.core.config.scheduledExecutor;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.config.scheduledExecutor.conf.BaseScheduledExecutorService;
import com.llfy.cesea.core.config.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时器服务
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class ScheduledExecutorInitService {

    private final RedisUtil redisUtil;

    private final String appName;

    private final BaseScheduledExecutorService baseScheduledExecutorService;

    public ScheduledExecutorInitService(
            @Value("${app.name}") String appName,
            RedisUtil redisUtil,
            BaseScheduledExecutorService baseScheduledExecutorService) {
        this.appName = appName;
        this.redisUtil = redisUtil;
        this.baseScheduledExecutorService = baseScheduledExecutorService;
    }

    /**
     * 初始化任务
     */
    public void initializeTask() {
        //启动心跳检测
        baseScheduledExecutorService.heartbeatDetection();
        //故障转移检测
        baseScheduledExecutorService.failover();
        //获取缓存池中当前机器所有任务
        List<ScheduledFutureDto> scheduledFutureDtoList = JSON.parseArray(redisUtil.hValues(appName).toString(), ScheduledFutureDto.class);
        for (ScheduledFutureDto scheduledFutureDto : scheduledFutureDtoList) {
            baseScheduledExecutorService.restart(scheduledFutureDto);
        }

    }
}
