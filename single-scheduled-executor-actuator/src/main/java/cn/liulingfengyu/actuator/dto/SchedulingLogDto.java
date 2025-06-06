package cn.liulingfengyu.actuator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingLogDto {

    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String appName;
    /**
     * 完成状态
     */
    @Schema(description = "完成状态")
    private Boolean done;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startCreateTime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endCreateTime;
}
