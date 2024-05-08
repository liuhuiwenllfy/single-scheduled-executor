package cn.liulingfengyu.scheduledTask.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingLogVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private String taskId;
    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String appName;
    /**
     * 携带参数
     */
    @Schema(description = "携带参数")
    private String taskParam;
    /**
     * 完成状态
     */
    @Schema(description = "完成状态")
    private boolean done;
    /**
     * 响应结果
     */
    @Schema(description = "响应结果")
    private String responseResult;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;
}
