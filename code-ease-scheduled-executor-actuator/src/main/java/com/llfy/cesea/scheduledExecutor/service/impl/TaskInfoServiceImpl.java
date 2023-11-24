package com.llfy.cesea.scheduledExecutor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.mapper.TaskInfoMapper;
import com.llfy.cesea.scheduledExecutor.service.ITaskInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public void saveItem(TaskInfo taskInfo) {
        this.saveOrUpdate(taskInfo);
    }

    @Override
    public void deleteItem(String id) {
        this.removeById(id);
    }

    @Override
    public List<TaskInfo> getRestartListExcludeAppointTask(List<String> idList) {
        return baseMapper.getRestartListExcludeAppointTask(idList).stream().filter(taskInfo -> !idList.contains(taskInfo.getId())).collect(Collectors.toList());
    }
}
