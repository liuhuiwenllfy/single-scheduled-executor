package cn.liulingfengyu.scheduledTask;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("cn.liulingfengyu.scheduledTask.mapper")
@OpenAPIDefinition(info = @Info(title = "single-scheduled-executor", description = "定时器任务框架", version = "1.0.0"))
public class SingleScheduledExecutorAdminApplication {
    public static void main(String[] args) {
        System.setProperty("druid.mysql.usePingMethod", "false");
        SpringApplication.run(SingleScheduledExecutorAdminApplication.class, args);
    }
}
