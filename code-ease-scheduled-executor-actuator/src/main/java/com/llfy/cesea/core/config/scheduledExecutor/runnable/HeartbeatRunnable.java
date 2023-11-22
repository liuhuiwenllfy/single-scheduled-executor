package com.llfy.cesea.core.config.scheduledExecutor.runnable;

import com.llfy.cesea.utils.RedisUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
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

    private String appName;

    private long appHeartbeatInterval;

    private RedisUtil redisUtil;

    public HeartbeatRunnable() {
    }

    public HeartbeatRunnable(
            String appName,
            long appHeartbeatInterval,
            RedisUtil redisUtil) {
        this.appName = appName;
        this.appHeartbeatInterval = appHeartbeatInterval;
        this.redisUtil = redisUtil;
    }

    @Override
    public void run() {
        redisUtil.hPut("actuator", appName, InetAddress.getLoopbackAddress().getHostAddress());
        redisUtil.setEx("actuatorHeartbeat:".concat(appName), InetAddress.getLoopbackAddress().getHostAddress(), appHeartbeatInterval, TimeUnit.MILLISECONDS);
        log.info("执行器{}心跳正常", appName);
    }
}
