package cn.liulingfengyu.actuator.service;


import cn.liulingfengyu.actuator.dto.TaskInsertDto;
import cn.liulingfengyu.actuator.dto.TaskUpdateDto;
import cn.liulingfengyu.actuator.entity.TaskInfo;

/**
 * 任务管理接口
 *
 * @author 刘凌枫羽工作室
 */
public interface IScheduledExecutorService {

    /**
     * 创建任务
     *
     * @param taskInsertDto 入参
     */
    void insertItem(TaskInsertDto taskInsertDto);

    /**
     * 启动任务
     *
     * @param id 入参
     */
    void start(String id);

    /**
     * 启动任务
     *
     * @param taskInfo 入参
     */
    void start(TaskInfo taskInfo);

    /**
     * 修改任务
     *
     * @param taskUpdateDto 入参
     */
    void updateItem(TaskUpdateDto taskUpdateDto);

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

}
