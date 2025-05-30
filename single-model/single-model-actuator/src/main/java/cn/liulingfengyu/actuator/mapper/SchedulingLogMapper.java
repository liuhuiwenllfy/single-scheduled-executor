package cn.liulingfengyu.actuator.mapper;

import cn.liulingfengyu.actuator.dto.SchedulingLogDto;
import cn.liulingfengyu.actuator.entity.SchedulingLog;
import cn.liulingfengyu.actuator.vo.SchedulingLogVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 调度日志表 Mapper 接口
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@DS("scheduled_task")
public interface SchedulingLogMapper extends BaseMapper<SchedulingLog> {

    /**
     * 条件分页查询日志
     *
     * @param page 分页参数
     * @return {@link IPage}
     */
    List<SchedulingLogVo> getByPage(
            @Param("page") Page<SchedulingLogVo> page,
            @Param("schedulingLogDto") SchedulingLogDto schedulingLogDto);

    /**
     * 删除三十天前的执行器日志
     */
    void deleteThirtyDaysAgoActuatorLogs();
}
