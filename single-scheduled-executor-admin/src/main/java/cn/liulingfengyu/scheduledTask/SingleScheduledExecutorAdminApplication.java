package cn.liulingfengyu.scheduledTask;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 启动类
 *
 * @author LLFY
 */
@SpringBootApplication
@ComponentScan(basePackages = "cn.liulingfengyu")
@MapperScan("cn.liulingfengyu.scheduledTask.mapper")
@EnableScheduling
public class SingleScheduledExecutorAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SingleScheduledExecutorAdminApplication.class, args);
    }
}
