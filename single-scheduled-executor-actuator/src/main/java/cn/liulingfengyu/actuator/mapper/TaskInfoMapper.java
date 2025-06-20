package cn.liulingfengyu.actuator.mapper;

import cn.liulingfengyu.actuator.dto.TaskInfoPageDto;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.vo.TaskInfoVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 条件分页查询任务
     *
     * @param taskInfoPageDto 分页参数
     * @return {@link List}
     */
    List<TaskInfoVo> getByPage(
            @Param("page") Page<TaskInfoVo> page,
            @Param("taskInfoPageDto") TaskInfoPageDto taskInfoPageDto);

    /**
     * 根据任务id查询任务
     *
     * @param id 任务id
     * @return {@link TaskInfoVo}
     */
    TaskInfoVo queryById(String id);
}
