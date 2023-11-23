package com.llfy.cesea.scheduledExecutor.dto;

import lombok.Getter;
import lombok.Setter;

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
    private String taskId;

    /**
     * 任务携带参数
     */
    private String taskParam;

    /**
     * 延迟执行时间；毫秒
     */
    private long initialDelay;

    /**
     * 是否为循环任务
     */
    private boolean periodic;

    /**
     * 间隔时间；毫秒
     */
    private long period;

}
