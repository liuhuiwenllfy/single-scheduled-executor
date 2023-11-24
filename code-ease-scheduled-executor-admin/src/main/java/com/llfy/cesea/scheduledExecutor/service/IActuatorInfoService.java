package com.llfy.cesea.scheduledExecutor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.llfy.cesea.scheduledExecutor.dto.ActuatorInfoDto;
import com.llfy.cesea.scheduledExecutor.entity.ActuatorInfo;

/**
 * <p>
 * 执行器 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface IActuatorInfoService extends IService<ActuatorInfo> {

    /**
     * 条件分页查询执行器
     *
     * @param page 分页参数
     * @return {@link IPage}
     */
    IPage<ActuatorInfo> getByPage(Page<ActuatorInfo> page, ActuatorInfoDto actuatorInfoDto);

}
