package com.llfy.cesea.scheduledExecutor.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * 保存执行器
     *
     * @param actuatorName 执行器名称
     * @param ip           ip
     */
    void saveItem(String actuatorName, String ip);

}
