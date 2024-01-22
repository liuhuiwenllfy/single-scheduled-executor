package cn.liulingfengyu.scheduledTask.service;

import cn.liulingfengyu.redis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HeartbeatService {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${app.name}")
    private String appName;

    @Value("${app.address}")
    private String appAddress;

    @Value("${business.name}")
    private String businessName;

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void checkHeartbeat() {
        //注册执行器节点
        redisUtil.hPut(businessName, appName, appAddress);
        redisUtil.setEx(businessName.concat("-").concat("heartbeat:").concat(appName), appAddress, 7000, TimeUnit.MILLISECONDS);
        log.info("调度中心{}心跳正常", appName);
    }
}