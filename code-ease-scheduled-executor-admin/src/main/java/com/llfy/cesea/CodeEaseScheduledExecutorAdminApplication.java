package com.llfy.cesea;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.llfy.cesea.*.mapper")
public class CodeEaseScheduledExecutorAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeEaseScheduledExecutorAdminApplication.class, args);
    }

}
