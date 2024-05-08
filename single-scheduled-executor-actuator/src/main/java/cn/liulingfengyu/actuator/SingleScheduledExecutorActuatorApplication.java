package cn.liulingfengyu.actuator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author 刘凌枫羽工作室
 */
@SpringBootApplication
@ComponentScan(basePackages = "cn.liulingfengyu")
@MapperScan("cn.liulingfengyu.actuator.mapper")
@EnableScheduling
public class SingleScheduledExecutorActuatorApplication {
    public static void main(String[] args) {
        System.setProperty("druid.mysql.usePingMethod", "false");
        SpringApplication.run(SingleScheduledExecutorActuatorApplication.class, args);
    }
}
