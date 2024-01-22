package cn.liulingfengyu.scheduledTask.service.impl;

import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.mybatisplus.utils.PageUtils;
import cn.liulingfengyu.scheduledTask.dto.SchedulingLogDto;
import cn.liulingfengyu.scheduledTask.entity.SchedulingLog;
import cn.liulingfengyu.scheduledTask.mapper.SchedulingLogMapper;
import cn.liulingfengyu.scheduledTask.service.ISchedulingLogService;
import cn.liulingfengyu.scheduledTask.vo.SchedulingLogVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}
