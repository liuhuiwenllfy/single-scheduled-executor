package com.llfy.cesea.scheduledExecutor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llfy.cesea.scheduledExecutor.dto.TaskInfoPageDto;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.mapper.TaskInfoMapper;
import com.llfy.cesea.scheduledExecutor.service.ITaskInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 任务基表 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements ITaskInfoService {

    @Override
    public IPage<TaskInfo> getByPage(Page<TaskInfo> page, TaskInfoPageDto taskInfoPageDto) {
        QueryWrapper<TaskInfo> taskInfoQueryWrapper = new QueryWrapper<>();
        taskInfoQueryWrapper.eq(taskInfoPageDto.getTitle() != null && !taskInfoPageDto.getTitle().isEmpty(), TaskInfo.TITLE, taskInfoPageDto.getTitle());
        return baseMapper.selectPage(page, taskInfoQueryWrapper);
    }
}
