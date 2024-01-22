package cn.liulingfengyu.scheduledTask.mapper;


import cn.liulingfengyu.scheduledTask.dto.TaskInfoPageDto;
import cn.liulingfengyu.scheduledTask.entity.TaskInfo;
import cn.liulingfengyu.scheduledTask.vo.TaskInfoVo;
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
