package cn.liulingfengyu.actuator.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "actuator")
public class ActuatorProperty {
    /**
     * 执行器名称
     */
    private String name;
    /**
     * 执行器ip
     */
    private String ip;
    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 心跳间隔
     */
    private Integer heartbeatInterval;

    public void setCorePoolSize(Integer corePoolSize) {
        if (corePoolSize == null) {
            corePoolSize = 2;
        }
        this.corePoolSize = corePoolSize;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        if (heartbeatInterval == null) {
            heartbeatInterval = 5000;
        }
        this.heartbeatInterval = heartbeatInterval;
    }
}
