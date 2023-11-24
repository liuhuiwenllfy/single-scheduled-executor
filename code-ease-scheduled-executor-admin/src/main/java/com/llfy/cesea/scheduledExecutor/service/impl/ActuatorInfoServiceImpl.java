package com.llfy.cesea.scheduledExecutor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llfy.cesea.scheduledExecutor.dto.ActuatorInfoDto;
import com.llfy.cesea.scheduledExecutor.entity.ActuatorInfo;
import com.llfy.cesea.scheduledExecutor.mapper.ActuatorInfoMapper;
import com.llfy.cesea.scheduledExecutor.service.IActuatorInfoService;
import org.springframework.stereotype.Service;

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
    public IPage<ActuatorInfo> getByPage(Page<ActuatorInfo> page, ActuatorInfoDto actuatorInfoDto) {
        QueryWrapper<ActuatorInfo> actuatorInfoQueryWrapper = new QueryWrapper<>();
        actuatorInfoQueryWrapper.eq(StringUtils.isNotBlank(actuatorInfoDto.getActuatorName()), ActuatorInfo.ACTUATOR_NAME, actuatorInfoDto.getActuatorName());
        actuatorInfoQueryWrapper.eq(StringUtils.isNotBlank(actuatorInfoDto.getActuatorIp()), ActuatorInfo.ACTUATOR_IP, actuatorInfoDto.getActuatorIp());
        return baseMapper.selectPage(page, actuatorInfoQueryWrapper);
    }
}
