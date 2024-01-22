package cn.liulingfengyu.actuator.scheduledExecutor.mapper;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 任务基表 Mapper 接口
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@DS("scheduled_task")
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

    /**
     * 获取任务并排除指定的任务
     *
     * @param idList 排除的任务
     * @return {@link List}
     */
    List<TaskInfo> getRestartListExcludeAppointTask(List<String> idList);

}
