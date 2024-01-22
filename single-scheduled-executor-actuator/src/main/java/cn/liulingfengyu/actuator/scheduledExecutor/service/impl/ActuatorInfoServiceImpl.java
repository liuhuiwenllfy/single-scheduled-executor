package cn.liulingfengyu.actuator.scheduledExecutor.service.impl;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.ActuatorInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.mapper.ActuatorInfoMapper;
import cn.liulingfengyu.actuator.scheduledExecutor.service.IActuatorInfoService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
@DS("scheduled_task")
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
