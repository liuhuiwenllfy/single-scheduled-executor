package com.llfy.cesea.scheduledExecutor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llfy.cesea.scheduledExecutor.entity.SchedulingLog;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;

/**
 * <p>
 * 调度日志表 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface ISchedulingLogService extends IService<SchedulingLog> {

    /**
     * 新增日志
     *
     * @param appName  执行器名称
     * @param taskInfo 入参
     */
    String insertItem(String appName, TaskInfo taskInfo);

    /**
     * 修改日志
     *
     * @param id       日志id
     * @param taskInfo 入参
     */
    void updateItem(String id, TaskInfo taskInfo);
}
