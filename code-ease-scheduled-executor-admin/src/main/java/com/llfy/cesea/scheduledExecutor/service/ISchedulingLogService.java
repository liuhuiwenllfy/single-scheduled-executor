package com.llfy.cesea.scheduledExecutor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.llfy.cesea.scheduledExecutor.dto.SchedulingLogDto;
import com.llfy.cesea.scheduledExecutor.entity.SchedulingLog;

/**
 * <p>
 * 调度日志表 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface ISchedulingLogService extends IService<SchedulingLog> {

    /**
     * 条件分页查询日志
     *
     * @param page 分页参数
     * @return {@link IPage}
     */
    IPage<SchedulingLog> getByPage(Page<SchedulingLog> page, SchedulingLogDto schedulingLogDto);

}
