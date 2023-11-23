package com.llfy.cesea.scheduledExecutor.service;


import com.llfy.cesea.scheduledExecutor.dto.TaskDto;

/**
 * 任务管理接口
 *
 * @author 刘凌枫羽工作室
 */
public interface IScheduledExecutorService {

    /**
     * 启动任务
     *
     * @param taskDto 入参
     */
    void start(TaskDto taskDto);

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
