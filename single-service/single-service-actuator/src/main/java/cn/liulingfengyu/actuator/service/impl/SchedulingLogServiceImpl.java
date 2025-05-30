package cn.liulingfengyu.actuator.service.impl;

import cn.liulingfengyu.actuator.dto.SchedulingLogDto;
import cn.liulingfengyu.actuator.entity.SchedulingLog;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.mapper.SchedulingLogMapper;
import cn.liulingfengyu.actuator.service.ISchedulingLogService;
import cn.liulingfengyu.actuator.vo.SchedulingLogVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.mybatisplus.utils.PageUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
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
    public IPage<SchedulingLogVo> getByPage(PageInfo pageInfo, SchedulingLogDto schedulingLogDto) {
        Page<SchedulingLogVo> page = new PageUtils<SchedulingLogVo>().getPage(pageInfo);
        return page.setRecords(baseMapper.getByPage(page, schedulingLogDto));
    }

    @Override
    @Async
    public void deleteThirtyDaysAgoActuatorLogs() {
        baseMapper.deleteThirtyDaysAgoActuatorLogs();
    }

    @Override
    public void insertItem(TaskInfo taskInfo) {
        SchedulingLog schedulingLog = new SchedulingLog();
        schedulingLog.setTaskId(taskInfo.getId());
        schedulingLog.setAppName(taskInfo.getAppName());
        schedulingLog.setTaskParam(taskInfo.getTaskParam());
        schedulingLog.setDone(taskInfo.isDone());
        schedulingLog.setCreateTime(new Date());
        schedulingLog.setTenantId(taskInfo.getTenantId());
        this.save(schedulingLog);
    }
}
