package com.llfy.cesea;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author 刘凌枫羽工作室
 */
@SpringBootApplication
@MapperScan("com.llfy.cesea.scheduledExecutor.*.mapper")
public class CodeEaseScheduledExecutorActuatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeEaseScheduledExecutorActuatorApplication.class, args);
    }
}
