package com.llfy.cesea.scheduledExecutor.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 任务实体
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
public class TaskDto implements Serializable {

    /**
     * 任务id
     */
    @NotBlank(message = "任务id不能为空")
    private String id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 是否为循环任务
     */
    private boolean periodic;

    /**
     * 延迟时间（毫秒）
     */
    private long initialDelay;

    /**
     * 间隔时间（毫秒）
     */
    private long period;

    /**
     * 任务携带参数
     */
    private String taskParam;

}
