package com.llfy.cesea.core.initialize;

import com.llfy.cesea.scheduledExecutor.ScheduledExecutorInitService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class BaseCommandLineRunner implements CommandLineRunner {

    private final ScheduledExecutorInitService scheduledExecutorInitService;

    public BaseCommandLineRunner(ScheduledExecutorInitService scheduledExecutorInitService) {
        this.scheduledExecutorInitService = scheduledExecutorInitService;
    }

    @Override
    public void run(String... args) {
        scheduledExecutorInitService.initializeTask();
    }
}