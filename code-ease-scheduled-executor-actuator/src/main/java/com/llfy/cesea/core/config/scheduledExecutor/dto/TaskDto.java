package com.llfy.cesea.core.config.scheduledExecutor.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * 任务实体
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
public class TaskDto implements Serializable {

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务携带参数")
    private String taskParam;

    @ApiModelProperty("延迟执行时间；毫秒")
    private long initialDelay;

    @ApiModelProperty("间隔时间；毫秒")
    private long period;

    @ApiModelProperty("ip地址集合")
    private Set<String> ipSet;

    @ApiModelProperty("请求路由")
    private String url;

}
