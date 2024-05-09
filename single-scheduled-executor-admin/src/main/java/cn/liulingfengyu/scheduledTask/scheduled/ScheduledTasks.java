package cn.liulingfengyu.scheduledTask.scheduled;

import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${app.name}")
    private String appName;

    /**
     * 每隔5秒执行一次
     */
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        //心跳
        redisUtil.setEx(RedisConstant.ADMIN_HEARTBEAT.concat(appName), appName, 10000, TimeUnit.MILLISECONDS);
        //注册
        redisUtil.hPut(RedisConstant.ADMIN_REGISTRY, appName, appName);
        //清理注册表
        redisUtil.hGetAll(RedisConstant.ADMIN_REGISTRY).values().forEach(s -> {
            if (!redisUtil.hasKey(RedisConstant.ADMIN_HEARTBEAT.concat((String) s))) {
                //删除注册表
                redisUtil.hDelete(RedisConstant.ADMIN_REGISTRY, s);
            }
        });
        log.info("调度中心{}心跳正常", appName);
    }
}