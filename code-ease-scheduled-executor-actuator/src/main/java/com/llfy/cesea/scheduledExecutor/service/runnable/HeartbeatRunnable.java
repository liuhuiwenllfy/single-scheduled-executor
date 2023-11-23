package com.llfy.cesea.scheduledExecutor.service.runnable;

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

    private String actuatorName;

    private String appName;

    private long appHeartbeatInterval;

    private RedisUtil redisUtil;

    private String port;

    public HeartbeatRunnable() {
    }

    public HeartbeatRunnable(
            String actuatorName,
            String appName,
            long appHeartbeatInterval,
            RedisUtil redisUtil,
            String port) {
        this.actuatorName = actuatorName;
        this.appName = appName;
        this.appHeartbeatInterval = appHeartbeatInterval;
        this.redisUtil = redisUtil;
        this.port = port;
    }

    @Override
    public void run() {
        //注册执行器节点
        redisUtil.hPut(actuatorName, appName, InetAddress.getLoopbackAddress().getHostAddress().concat(":").concat(port));
        redisUtil.setEx(actuatorName.concat("-").concat("heartbeat:").concat(appName), InetAddress.getLoopbackAddress().getHostAddress(), appHeartbeatInterval, TimeUnit.MILLISECONDS);
        log.info("执行器{}心跳正常", appName);
    }
}
