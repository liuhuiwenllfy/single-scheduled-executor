package com.llfy.cesea.core.config.initialize;

import com.llfy.cesea.core.config.redis.RedisInitService;
import com.llfy.cesea.core.config.scheduledExecutor.ScheduledExecutorInitService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class BaseCommandLineRunner implements CommandLineRunner {

    private final RedisInitService redisInitService;

    private final ScheduledExecutorInitService scheduledExecutorInitService;

    public BaseCommandLineRunner(RedisInitService redisInitService, ScheduledExecutorInitService scheduledExecutorInitService) {
        this.redisInitService = redisInitService;
        this.scheduledExecutorInitService = scheduledExecutorInitService;
    }

    @Override
    public void run(String... args) {
        redisInitService.initializeRedis();
        scheduledExecutorInitService.initializeTask();
    }
}