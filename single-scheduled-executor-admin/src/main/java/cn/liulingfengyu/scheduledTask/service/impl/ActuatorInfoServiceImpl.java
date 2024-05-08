package cn.liulingfengyu.scheduledTask.service.impl;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import cn.liulingfengyu.scheduledTask.dto.ActuatorInfoDto;
import cn.liulingfengyu.scheduledTask.service.IActuatorInfoService;
import cn.liulingfengyu.scheduledTask.vo.ActuatorInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<String> actuatorInfoList = JSONUtil.toList(JSONUtil.toJsonStr(redisUtil.hGetAll(RedisConstant.ACTUATOR_REGISTRY).values()), String.class);
        List<ActuatorInfoVo> list = new ArrayList<>();
        actuatorInfoList.forEach(item -> {
            ActuatorInfoVo actuatorInfoVo = new ActuatorInfoVo();
            actuatorInfoVo.setActuatorName(item);
            actuatorInfoVo.setIsNormal(redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(item)));
            list.add(actuatorInfoVo);
        });
        return list;
    }

    @Override
    public boolean deleteBatchByIdList(List<String> idList) {
        return redisUtil.hDelete(RedisConstant.ACTUATOR_REGISTRY, idList.toArray(new Object[0])) > 0;
    }
}
