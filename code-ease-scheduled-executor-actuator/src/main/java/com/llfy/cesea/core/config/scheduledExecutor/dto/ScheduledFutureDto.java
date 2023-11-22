package com.llfy.cesea.core.config.scheduledExecutor.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 任务和执行情况实体
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
public class ScheduledFutureDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("是否已取消")
    private boolean cancelled;

    @ApiModelProperty("是否已完成")
    private boolean done;

    @ApiModelProperty("是否为循环任务")
    private boolean periodic;

    @ApiModelProperty("任务信息")
    private TaskDto taskDto;

    @ApiModelProperty("下一次执行时间；毫秒")
    private long nextExecutionTime;

}
