package cn.liulingfengyu.actuator.scheduledExecutor.service;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.ActuatorInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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
