package com.llfy.cesea.scheduledExecutor.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llfy.cesea.scheduledExecutor.dto.SchedulingLogDto;
import com.llfy.cesea.scheduledExecutor.entity.SchedulingLog;
import com.llfy.cesea.scheduledExecutor.mapper.SchedulingLogMapper;
import com.llfy.cesea.scheduledExecutor.service.ISchedulingLogService;
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
public class SchedulingLogServiceImpl extends ServiceImpl<SchedulingLogMapper, SchedulingLog> implements ISchedulingLogService {

    @Override
    public IPage<SchedulingLog> getByPage(Page<SchedulingLog> page, SchedulingLogDto schedulingLogDto) {
        return baseMapper.getByPage(page, schedulingLogDto);
    }
}
