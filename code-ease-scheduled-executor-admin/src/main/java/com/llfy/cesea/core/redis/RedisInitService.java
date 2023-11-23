package com.llfy.cesea.core.redis;

import com.llfy.cesea.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class RedisInitService {

    private final RedisUtil redisUtil;

    private final String businessName;

    private final String appName;

    private final String port;

    public RedisInitService(RedisUtil redisUtil,
                            @Value("${business.name}") String businessName,
                            @Value("${app.name}") String appName,
                            @Value("${server.port}") String port) {
        this.redisUtil = redisUtil;
        this.businessName = businessName;
        this.appName = appName;
        this.port = port;
    }

    /**
     * 初始化参数
     */
    public void initializeRedis() {
        //注册业务节点
        redisUtil.hPut(businessName, appName, InetAddress.getLoopbackAddress().getHostAddress().concat(":").concat(port));
    }
}
