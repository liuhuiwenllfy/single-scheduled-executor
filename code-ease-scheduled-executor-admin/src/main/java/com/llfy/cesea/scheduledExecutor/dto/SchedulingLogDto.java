package com.llfy.cesea.scheduledExecutor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingLogDto {

    /**
     * 任务id
     */
    private String taskId;
    /**
     * 执行器名称
     */
    private String appName;
    /**
     * 完成状态
     */
    private Boolean done;
    /**
     * 响应结果
     */
    private String responseResult;
    /**
     * 开始时间
     */
    private String startCreateTime;
    /**
     * 结束时间
     */
    private String endCreateTime;
    /**
     * 租户id
     */
    private String tenantId;
}
