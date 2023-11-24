package com.llfy.cesea.scheduledExecutor.service.runnable;

import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
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

    private BaseScheduledExecutorService base;

    public HeartbeatRunnable() {
    }

    public HeartbeatRunnable(BaseScheduledExecutorService base) {
        this.base = base;
    }

    @Override
    public void run() {
        //注册执行器节点
        String ip = InetAddress.getLoopbackAddress().getHostAddress().concat(":").concat(base.port);
        base.redisUtil.hPut(base.actuatorName, base.appName, ip);
        base.redisUtil.setEx(base.actuatorName.concat("-").concat("heartbeat:").concat(base.appName), ip, base.appHeartbeatInterval, TimeUnit.MILLISECONDS);
        //持久化
        if (base.persistence) {
            base.actuatorInfoService.saveItem(base.appName, ip);
        }
        log.info("执行器{}心跳正常", base.appName);
    }
}
