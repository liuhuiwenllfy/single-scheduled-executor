package com.llfy.cesea.scheduledExecutor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llfy.cesea.scheduledExecutor.dto.SchedulingLogDto;
import com.llfy.cesea.scheduledExecutor.entity.SchedulingLog;
import com.llfy.cesea.scheduledExecutor.service.ISchedulingLogService;
import com.llfy.cesea.utils.RespJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 调度日志表 前端控制器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/scheduledExecutor/schedulingLog")
public class SchedulingLogController {

    @Autowired
    private ISchedulingLogService schedulingLogService;

    /**
     * 条件分页查询日志
     *
     * @param page 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    public RespJson<IPage<SchedulingLog>> getByPage(Page<SchedulingLog> page, SchedulingLogDto schedulingLogDto) {
        return RespJson.success(schedulingLogService.getByPage(page, schedulingLogDto));
    }
}
