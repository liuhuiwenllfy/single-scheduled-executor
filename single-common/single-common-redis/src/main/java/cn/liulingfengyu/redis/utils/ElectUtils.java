package cn.liulingfengyu.redis.utils;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.redis.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ElectUtils {

    @Autowired
    private RedisUtil redisUtil;

    public String adminElectUtils() {
        Set<String> adminCollect = JSONUtil.toList(JSONUtil.toJsonStr(redisUtil.hGetAll(RedisConstant.ADMIN_REGISTRY).values()), String.class).stream().filter(s -> redisUtil.hasKey(RedisConstant.ADMIN_HEARTBEAT.concat(s))).collect(Collectors.toSet());
        if (adminCollect.isEmpty()) {
            throw new RuntimeException("没有可用的调度中心");
        }
        Random random = new Random();
        return adminCollect.toArray()[random.nextInt(adminCollect.size())].toString();
    }

    public String actuatorElectUtils() {
        Set<String> actuatorCollect = JSONUtil.toList(JSONUtil.toJsonStr(redisUtil.hGetAll(RedisConstant.ACTUATOR_REGISTRY).values()), String.class).stream().filter(s -> redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(s))).collect(Collectors.toSet());
        if (actuatorCollect.isEmpty()) {
            throw new RuntimeException("没有可用的执行器");
        }
        Random random = new Random();
        return actuatorCollect.toArray()[random.nextInt(actuatorCollect.size())].toString();
    }

}
