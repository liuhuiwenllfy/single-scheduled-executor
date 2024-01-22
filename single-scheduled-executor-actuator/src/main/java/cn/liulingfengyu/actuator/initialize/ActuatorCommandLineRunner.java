package cn.liulingfengyu.actuator.initialize;

import cn.liulingfengyu.actuator.scheduledExecutor.service.ScheduledExecutorInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class ActuatorCommandLineRunner implements CommandLineRunner {

    @Autowired
    private ScheduledExecutorInitService scheduledExecutorInitService;

    @Override
    public void run(String... args) {
        scheduledExecutorInitService.initializeTask();
    }
}