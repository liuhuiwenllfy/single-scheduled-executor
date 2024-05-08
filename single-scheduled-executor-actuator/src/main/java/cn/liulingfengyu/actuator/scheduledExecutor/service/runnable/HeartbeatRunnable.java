package cn.liulingfengyu.actuator.scheduledExecutor.service.runnable;

import cn.liulingfengyu.actuator.property.ActuatorProperty;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.redis.constant.RedisConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 心跳检测执行函数
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
@Slf4j
@Component
public class HeartbeatRunnable implements Runnable {

    private BaseScheduledExecutorService base;

    private ActuatorProperty actuatorProperty;

    public HeartbeatRunnable(BaseScheduledExecutorService base,
                             ActuatorProperty actuatorProperty) {
        this.base = base;
        this.actuatorProperty = actuatorProperty;
    }

    @Override
    public void run() {
        base.redisUtil.setEx(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorProperty.getName()), actuatorProperty.getName(), actuatorProperty.getHeartbeatInterval() + 2000, TimeUnit.MILLISECONDS);
        base.actuatorInfoService.saveItem(actuatorProperty.getName(), actuatorProperty.getIp());
        //任务恢复
        base.restart(true);
        log.info("执行器{}心跳正常", actuatorProperty.getName());
    }
}
