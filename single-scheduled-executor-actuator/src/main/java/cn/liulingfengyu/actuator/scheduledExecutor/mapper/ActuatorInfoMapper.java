package cn.liulingfengyu.actuator.scheduledExecutor.mapper;

import cn.liulingfengyu.actuator.scheduledExecutor.entity.ActuatorInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 执行器 Mapper 接口
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@DS("scheduled_task")
public interface ActuatorInfoMapper extends BaseMapper<ActuatorInfo> {

}
