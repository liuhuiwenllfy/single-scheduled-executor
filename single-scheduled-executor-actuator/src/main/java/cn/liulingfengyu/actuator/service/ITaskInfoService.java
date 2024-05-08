package cn.liulingfengyu.actuator.service;

import cn.liulingfengyu.actuator.entity.TaskInfo;
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
     * 获取指定执行器需要重启的任务
     *
     * @param actuatorName 执行器名称
     * @return {@link List}
     */
    List<TaskInfo> getRestartList(String actuatorName);


}
