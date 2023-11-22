package com.llfy.cesea.core.config.scheduledExecutor.dto;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("执行器名称")
    private String appName;

    @ApiModelProperty("任务信息")
    private List<ScheduledFutureDto> scheduledFutureDtoList;

    @ApiModelProperty("消息类型")
    private String incident;
}
