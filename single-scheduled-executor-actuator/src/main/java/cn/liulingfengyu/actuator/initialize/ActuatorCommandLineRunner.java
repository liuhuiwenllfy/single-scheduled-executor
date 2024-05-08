package cn.liulingfengyu.actuator.initialize;

import cn.liulingfengyu.actuator.scheduledExecutor.service.ScheduledExecutorInitService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class ActuatorCommandLineRunner implements CommandLineRunner {

    private final ScheduledExecutorInitService scheduledExecutorInitService;

    public ActuatorCommandLineRunner(ScheduledExecutorInitService scheduledExecutorInitService) {
        this.scheduledExecutorInitService = scheduledExecutorInitService;
    }

    @Override
    public void run(String... args) {
        scheduledExecutorInitService.initializeTask();
    }
}