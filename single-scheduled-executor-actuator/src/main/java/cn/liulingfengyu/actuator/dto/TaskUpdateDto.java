package cn.liulingfengyu.actuator.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDto {

    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 代码
     */
    @Schema(description = "代码")
    @NotBlank(message = "代码不能为空")
    @Size(min = 1, max = 90, message = "代码长度在1~90字符之间")
    private String code;

    /**
     * 标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 90, message = "标题长度在1~90字符之间")
    private String title;

    /**
     * cron
     */
    @Schema(description = "cron")
    @NotBlank(message = "cron不能为空")
    private String cron;

    /**
     * 任务携带参数
     */
    @Schema(description = "任务携带参数")
    private String taskParam;
}
