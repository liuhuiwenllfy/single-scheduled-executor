package cn.liulingfengyu.actuator;

import cn.liulingfengyu.actuator.support.MyScheduledExecutorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private MyScheduledExecutorService myScheduledExecutorService;

    @Override
    public void run(String... args) {
        log.info("应用启动成功，执行初始化操作...");
        load();
    }

    public void load() {
        log.info("重启任务...");
        myScheduledExecutorService.restartTask(myScheduledExecutorService.actuatorName);
    }
}
