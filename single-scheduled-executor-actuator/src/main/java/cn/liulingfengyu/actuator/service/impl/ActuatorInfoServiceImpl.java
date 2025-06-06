package cn.liulingfengyu.actuator.service.impl;

import cn.liulingfengyu.actuator.dto.ActuatorInfoDto;
import cn.liulingfengyu.actuator.service.IActuatorInfoService;
import cn.liulingfengyu.actuator.vo.ActuatorInfoVo;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 执行器 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
public class ActuatorInfoServiceImpl implements IActuatorInfoService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<ActuatorInfoVo> getList(ActuatorInfoDto actuatorInfoDto) {
        Set<String> keys = redisUtil.keys(RedisConstant.ACTUATOR_REGISTRY.concat("*"));
        List<ActuatorInfoVo> list = new ArrayList<>();
        keys.forEach(key -> {
            String name = redisUtil.get(key);
            ActuatorInfoVo actuatorInfoVo = new ActuatorInfoVo();
            actuatorInfoVo.setActuatorName(name);
            actuatorInfoVo.setNormal(redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(name)));
            list.add(actuatorInfoVo);
        });
        return list;
    }

    @Override
    public boolean deleteBatchByIdList(List<String> actuatorNames) {
        actuatorNames.forEach(actuatorName -> {
            redisUtil.delete(RedisConstant.ACTUATOR_REGISTRY.concat(actuatorName));
        });
        return true;
    }
}
