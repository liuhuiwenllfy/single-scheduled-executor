package cn.liulingfengyu.actuator.scheduledExecutor.service.impl;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.SchedulingLog;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.mapper.SchedulingLogMapper;
import cn.liulingfengyu.actuator.scheduledExecutor.service.ISchedulingLogService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 调度日志表 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
@DS("scheduled_task")
public class SchedulingLogServiceImpl extends ServiceImpl<SchedulingLogMapper, SchedulingLog> implements ISchedulingLogService {

    @Override
    public String insertItem(TaskInfo taskInfo) {
        SchedulingLog schedulingLog = new SchedulingLog();
        schedulingLog.setTaskId(taskInfo.getId());
        schedulingLog.setAppName(taskInfo.getAppName());
        schedulingLog.setTaskParam(taskInfo.getTaskParam());
        schedulingLog.setDone(taskInfo.isDone());
        schedulingLog.setCreateTime(new Date());
        schedulingLog.setTenantId(taskInfo.getTenantId());
        this.save(schedulingLog);
        return schedulingLog.getId();
    }
}
