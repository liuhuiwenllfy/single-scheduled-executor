package cn.liulingfengyu.scheduledTask.service.impl;

import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.mybatisplus.utils.PageUtils;
import cn.liulingfengyu.redis.utils.RedisUtil;
import cn.liulingfengyu.scheduledTask.dto.ActuatorInfoDto;
import cn.liulingfengyu.scheduledTask.entity.ActuatorInfo;
import cn.liulingfengyu.scheduledTask.mapper.ActuatorInfoMapper;
import cn.liulingfengyu.scheduledTask.service.IActuatorInfoService;
import cn.liulingfengyu.scheduledTask.vo.ActuatorInfoVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
@DS("scheduled_task")
public class ActuatorInfoServiceImpl extends ServiceImpl<ActuatorInfoMapper, ActuatorInfo> implements IActuatorInfoService {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${actuator.name}")
    private String actuatorName;

    @Override
    public IPage<ActuatorInfoVo> getByPage(PageInfo pageInfo, ActuatorInfoDto actuatorInfoDto) {
        Page<ActuatorInfoVo> page = new PageUtils<ActuatorInfoVo>().getPage(pageInfo);
        List<ActuatorInfoVo> list = baseMapper.getByPage(page, actuatorInfoDto);
        list.forEach(item -> item.setIsNormal(redisUtil.hasKey(actuatorName.concat("-").concat("heartbeat:").concat(item.getActuatorName()))));
        return page.setRecords(list);
    }

    @Override
    public boolean deleteBatchByIdList(List<String> idList) {
        if (baseMapper.deleteBatchIds(idList) > 0) {
            QueryWrapper<ActuatorInfo> actuatorInfoQueryWrapper = new QueryWrapper<>();
            actuatorInfoQueryWrapper.in(ActuatorInfo.ID, idList);
            List<ActuatorInfo> actuatorInfos = baseMapper.selectList(actuatorInfoQueryWrapper);
            Object[] actuatorNames = actuatorInfos.stream().map(ActuatorInfo::getActuatorName).toArray(Object[]::new);
            redisUtil.hDelete(actuatorName, Arrays.toString(actuatorNames));
            return true;
        }
        return false;
    }
}
