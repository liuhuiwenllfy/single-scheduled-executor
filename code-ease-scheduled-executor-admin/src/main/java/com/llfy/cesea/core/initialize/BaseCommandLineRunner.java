package com.llfy.cesea.core.initialize;

import com.llfy.cesea.core.redis.RedisInitService;
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

    public BaseCommandLineRunner(RedisInitService redisInitService) {
        this.redisInitService = redisInitService;
    }


    @Override
    public void run(String... args) {
        redisInitService.initializeRedis();
    }
}