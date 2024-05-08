package cn.liulingfengyu.actuator.scheduledExecutor.mapper;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
     * 获取指定执行器需要重启的任务
     *
     * @param actuatorName 执行器名称
     * @return {@link List}
     */
    List<TaskInfo> getRestartList(@Param("actuatorName") String actuatorName);

}
