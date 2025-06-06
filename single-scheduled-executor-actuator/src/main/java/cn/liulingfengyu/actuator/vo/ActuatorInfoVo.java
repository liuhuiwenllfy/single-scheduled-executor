package cn.liulingfengyu.actuator.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorInfoVo {

    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String actuatorName;

    /**
     * 是否正常
     */
    @Schema(description = "是否正常")
    private boolean isNormal;
}
