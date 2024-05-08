package cn.liulingfengyu.scheduledTask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Schema(name = "TaskInsertDto")
public class TaskInsertDto {

    /**
     * 代码
     */
    @Schema(description = "代码")
    @NotBlank(message = "code.empty")
    @Size(min = 1, max = 90, message = "code.length")
    private String code;

    /**
     * 标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "title.empty")
    @Size(min = 1, max = 90, message = "title.length")
    private String title;

    /**
     * cron
     */
    @Schema(description = "cron")
    private String cron;

    /**
     * 任务携带参数
     */
    @Schema(description = "任务携带参数")
    private String taskParam;
}
