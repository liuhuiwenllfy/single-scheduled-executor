package cn.liulingfengyu.scheduledTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * 启动类
 *
 * @author LLFY
 */
@SpringBootApplication
@ComponentScan(basePackages = "cn.liulingfengyu")
public class SingleScheduledExecutorAdminApplication {
    public static void main(String[] args) {
        System.setProperty("druid.mysql.usePingMethod", "false");
        SpringApplication.run(SingleScheduledExecutorAdminApplication.class, args);
    }
}
