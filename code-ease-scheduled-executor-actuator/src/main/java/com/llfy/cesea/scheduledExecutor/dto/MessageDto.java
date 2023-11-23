package com.llfy.cesea.scheduledExecutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 发布Redis消息实体
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
public class MessageDto {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 执行器名称
     */
    private String appName;

    /**
     * 任务信息
     */
    private List<TaskDto> taskDtoList;

    /**
     * 任务信息
     */
    private List<ScheduledFutureDto> scheduledFutureDtoList;

    /**
     * 消息类型
     */
    private String incident;
}
