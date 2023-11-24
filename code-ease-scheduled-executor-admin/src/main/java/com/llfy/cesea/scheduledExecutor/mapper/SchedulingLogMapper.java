package com.llfy.cesea.scheduledExecutor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llfy.cesea.scheduledExecutor.dto.SchedulingLogDto;
import com.llfy.cesea.scheduledExecutor.entity.SchedulingLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 调度日志表 Mapper 接口
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Mapper
public interface SchedulingLogMapper extends BaseMapper<SchedulingLog> {

    /**
     * 条件分页查询日志
     *
     * @param page 分页参数
     * @return {@link IPage}
     */
    IPage<SchedulingLog> getByPage(@Param("page") Page<SchedulingLog> page, @Param("schedulingLogDto") SchedulingLogDto schedulingLogDto);

}
