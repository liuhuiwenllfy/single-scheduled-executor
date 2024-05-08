package cn.liulingfengyu.scheduledTask.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorInfoVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String actuatorName;
    /**
     * 执行器ip
     */
    @Schema(description = "执行器ip")
    private String actuatorIp;

    /**
     * 是否正常
     */
    @Schema(description = "是否正常")
    private Boolean isNormal;
}
