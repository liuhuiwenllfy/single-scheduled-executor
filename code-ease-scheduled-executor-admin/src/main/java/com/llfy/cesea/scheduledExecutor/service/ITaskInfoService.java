package com.llfy.cesea.scheduledExecutor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.llfy.cesea.scheduledExecutor.dto.TaskInfoPageDto;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;

/**
 * <p>
 * 任务基表 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface ITaskInfoService extends IService<TaskInfo> {

    /**
     * 条件分页查询任务
     *
     * @param page            分页参数
     * @param taskInfoPageDto 分页参数
     * @return {@link IPage}
     */
    IPage<TaskInfo> getByPage(Page<TaskInfo> page, TaskInfoPageDto taskInfoPageDto);

}
