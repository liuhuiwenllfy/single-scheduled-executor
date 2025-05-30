package cn.liulingfengyu.actuator;

import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${actuator.name}")
    private String actuatorName;

    @Override
    public void run(String... args) {
        log.info("应用启动成功，执行初始化操作...");
        load();
    }

    public void load() {
        log.info("注册...");
        boolean main = redisUtil.hGetAll(RedisConstant.TASK_REGISTRY).containsValue("MAIN");
        redisUtil.hPut(RedisConstant.TASK_REGISTRY, actuatorName, "MAIN");
    }
}
