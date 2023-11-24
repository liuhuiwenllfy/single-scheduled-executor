package com.llfy.cesea.scheduledExecutor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llfy.cesea.scheduledExecutor.entity.ActuatorInfo;
import com.llfy.cesea.scheduledExecutor.mapper.ActuatorInfoMapper;
import com.llfy.cesea.scheduledExecutor.service.IActuatorInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 执行器 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
public class ActuatorInfoServiceImpl extends ServiceImpl<ActuatorInfoMapper, ActuatorInfo> implements IActuatorInfoService {

    @Override
    @Transactional
    public synchronized void saveItem(String actuatorName, String ip) {
        QueryWrapper<ActuatorInfo> actuatorInfoQueryWrapper = new QueryWrapper<>();
        actuatorInfoQueryWrapper.eq(ActuatorInfo.ACTUATOR_NAME, actuatorName);
        ActuatorInfo actuatorInfo = baseMapper.selectOne(actuatorInfoQueryWrapper);
        if (actuatorInfo == null) {
            actuatorInfo = new ActuatorInfo();
            actuatorInfo.setCreateTime(new Date());
        }
        actuatorInfo.setActuatorName(actuatorName);
        actuatorInfo.setActuatorIp(ip);
        this.saveOrUpdate(actuatorInfo);
    }
}
