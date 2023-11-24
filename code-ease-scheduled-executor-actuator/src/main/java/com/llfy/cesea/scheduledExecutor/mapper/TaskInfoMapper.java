package com.llfy.cesea.scheduledExecutor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;

import java.util.List;

/**
 * <p>
 * 任务基表 Mapper 接口
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

    /**
     * 获取任务并排除指定的任务
     *
     * @param idList 排除的任务
     * @return {@link List}
     */
    List<TaskInfo> getRestartListExcludeAppointTask(List<String> idList);

}
