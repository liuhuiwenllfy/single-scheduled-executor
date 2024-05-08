package cn.liulingfengyu.actuator.scheduledExecutor.service;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.SchedulingLog;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * @param taskInfo 入参
     */
    String insertItem(TaskInfo taskInfo);

}
