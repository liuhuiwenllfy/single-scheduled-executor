package com.llfy.cesea.page.scheduledExecutor.service;

import com.llfy.cesea.core.config.scheduledExecutor.dto.TaskDto;

/**
 * 任务管理接口
 *
 * @author 刘凌枫羽工作室
 */
public interface IScheduledExecutorService {

    /**
     * 启动单次任务
     *
     * @param taskDto 入参
     */
    void startOnce(TaskDto taskDto);

    /**
     * 启动循环任务
     *
     * @param taskDto 入参
     */
    void startLoop(TaskDto taskDto);

    /**
     * 暂停任务
     *
     * @param taskId 任务id
     */
    void stop(String taskId);

    /**
     * 删除任务
     *
     * @param taskId 任务id
     */
    void remove(String taskId);

    /**
     * 更新状态
     *
     * @param taskId 任务id
     */
    void updateStatus(String taskId);

}
