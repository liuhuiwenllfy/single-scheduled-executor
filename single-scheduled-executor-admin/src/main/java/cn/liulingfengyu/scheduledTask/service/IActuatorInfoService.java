package cn.liulingfengyu.scheduledTask.service;

import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.ActuatorInfoDto;
import cn.liulingfengyu.scheduledTask.entity.ActuatorInfo;
import cn.liulingfengyu.scheduledTask.vo.ActuatorInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
     * 条件分页查询执行器
     *
     * @param pageInfo 分页参数
     * @return {@link IPage}
     */
    IPage<ActuatorInfoVo> getByPage(PageInfo pageInfo, ActuatorInfoDto actuatorInfoDto);

    /**
     * 批量删除执行器
     * @param idList 执行器ids
     * @return boolean
     */
    boolean deleteBatchByIdList(List<String> idList);



}
