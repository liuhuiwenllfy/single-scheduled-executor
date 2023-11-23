package com.llfy.cesea.scheduledExecutor.service.impl;

import com.llfy.cesea.scheduledExecutor.entity.ActuatorInfo;
import com.llfy.cesea.scheduledExecutor.mapper.ActuatorInfoMapper;
import com.llfy.cesea.scheduledExecutor.service.IActuatorInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
