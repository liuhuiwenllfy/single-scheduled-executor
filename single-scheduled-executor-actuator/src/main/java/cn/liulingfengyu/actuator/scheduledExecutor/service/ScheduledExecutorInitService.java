package cn.liulingfengyu.actuator.scheduledExecutor.service;

import org.springframework.stereotype.Component;

/**
 * 定时器初始化
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class ScheduledExecutorInitService {

    private final BaseScheduledExecutorService base;

    public ScheduledExecutorInitService(BaseScheduledExecutorService base) {
        this.base = base;
    }

    /**
     * 初始化任务
     */
    public void initializeTask() {
        //启动心跳检测
        base.heartbeatDetection();
        //故障转移检测
        base.failover();
        //任务重启
        base.restart(false);
    }
}
