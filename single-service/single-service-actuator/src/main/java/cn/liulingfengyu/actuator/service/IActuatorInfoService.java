package cn.liulingfengyu.actuator.service;

import cn.liulingfengyu.actuator.dto.ActuatorInfoDto;
import cn.liulingfengyu.actuator.vo.ActuatorInfoVo;

import java.util.List;

/**
 * <p>
 * 执行器 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface IActuatorInfoService {

    /**
     * 条件查询执行器
     *
     * @param actuatorInfoDto 查询条件
     * @return {@link List}
     */
    List<ActuatorInfoVo> getList(ActuatorInfoDto actuatorInfoDto);

    /**
     * 批量删除执行器
     *
     * @param actuatorNames 执行器名称
     * @return boolean
     */
    boolean deleteBatchByIdList(List<String> actuatorNames);

}
