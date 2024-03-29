package cn.liulingfengyu.actuator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 *
 * @author 刘凌枫羽工作室
 */
@SpringBootApplication
@ComponentScan(basePackages = "cn.liulingfengyu")
@MapperScan("cn.liulingfengyu.actuator.*.mapper")
public class SingleScheduledExecutorActuatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SingleScheduledExecutorActuatorApplication.class, args);
    }
}
