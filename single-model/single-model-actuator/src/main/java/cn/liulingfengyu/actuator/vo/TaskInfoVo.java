package cn.liulingfengyu.actuator.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInfoVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 代码
     */
    @Schema(description = "代码")
    private String code;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String appName;
    /**
     * 任务携带参数
     */
    @Schema(description = "任务携带参数")
    private String taskParam;
    /**
     * 是否已取消
     */
    @Schema(description = "是否已取消")
    private boolean cancelled;
    /**
     * 是否已完成
     */
    @Schema(description = "是否已完成")
    private boolean done;
    /**
     * 下一次执行时间
     */
    @Schema(description = "下一次执行时间")
    private String nextExecutionTime;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * cron
     */
    @Schema(description = "cron")
    private String cron;
}
