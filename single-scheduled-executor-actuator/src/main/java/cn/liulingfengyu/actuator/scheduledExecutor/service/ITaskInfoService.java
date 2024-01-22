package cn.liulingfengyu.actuator.scheduledExecutor.service;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 任务基表 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface ITaskInfoService extends IService<TaskInfo> {

    /**
     * 新增任务
     *
     * @param taskInfo 入参
     */
    void saveItem(TaskInfo taskInfo);

    /**
     * 修改任务
     *
     * @param id 入参
     */
    void deleteItem(String id);

    /**
     * 获取任务并排除指定的任务
     *
     * @param idList 排除的任务
     * @return {@link List}
     */
    List<TaskInfo> getRestartListExcludeAppointTask(List<String> idList);


}
