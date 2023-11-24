package com.llfy.cesea.scheduledExecutor.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 任务基表 Mapper 接口
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

}
