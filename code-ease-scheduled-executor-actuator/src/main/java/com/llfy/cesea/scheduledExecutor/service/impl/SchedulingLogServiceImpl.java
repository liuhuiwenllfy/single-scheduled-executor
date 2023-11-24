package com.llfy.cesea.scheduledExecutor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llfy.cesea.scheduledExecutor.entity.SchedulingLog;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.mapper.SchedulingLogMapper;
import com.llfy.cesea.scheduledExecutor.service.ISchedulingLogService;
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
public class SchedulingLogServiceImpl extends ServiceImpl<SchedulingLogMapper, SchedulingLog> implements ISchedulingLogService {

    @Override
    public String insertItem(String appName, TaskInfo taskInfo) {
        SchedulingLog schedulingLog = new SchedulingLog();
        schedulingLog.setTaskId(taskInfo.getId());
        schedulingLog.setAppName(appName);
        schedulingLog.setTaskParam(taskInfo.getTaskParam());
        schedulingLog.setDone(taskInfo.isDone());
        schedulingLog.setCreateTime(new Date());
        schedulingLog.setTenantId(taskInfo.getTenantId());
        this.save(schedulingLog);
        return schedulingLog.getId();
    }

    @Override
    public void updateItem(String id, TaskInfo taskInfo) {
        SchedulingLog schedulingLog = new SchedulingLog();
        schedulingLog.setId(id);
        schedulingLog.setDone(taskInfo.isDone());
        this.updateById(schedulingLog);
    }
}
