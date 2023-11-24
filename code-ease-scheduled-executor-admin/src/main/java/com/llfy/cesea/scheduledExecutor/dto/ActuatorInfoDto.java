package com.llfy.cesea.scheduledExecutor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorInfoDto {

    /**
     * 执行器名称
     */
    private String actuatorName;
    /**
     * 执行器ip
     */
    private String actuatorIp;
}
