package cn.liulingfengyu.actuator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorInfoDto {

    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String actuatorName;
}
