package cn.liulingfengyu.actuator.scheduledExecutor.service.impl;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.mapper.TaskInfoMapper;
import cn.liulingfengyu.actuator.scheduledExecutor.service.ITaskInfoService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
@DS("scheduled_task")
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
