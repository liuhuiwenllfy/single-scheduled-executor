package cn.liulingfengyu.actuator.service.impl;

import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.mapper.TaskInfoMapper;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<TaskInfo> getRestartList(String actuatorName) {
        return baseMapper.getRestartList(actuatorName);
    }
}
