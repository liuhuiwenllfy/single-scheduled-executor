package cn.liulingfengyu.scheduledTask.mapper;

import cn.liulingfengyu.scheduledTask.dto.ActuatorInfoDto;
import cn.liulingfengyu.scheduledTask.entity.ActuatorInfo;
import cn.liulingfengyu.scheduledTask.vo.ActuatorInfoVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 条件分页查询执行器
     *
     * @param page 分页参数
     * @return {@link IPage}
     */
    List<ActuatorInfoVo> getByPage(@Param("page") Page<ActuatorInfoVo> page, @Param("actuatorInfoDto") ActuatorInfoDto actuatorInfoDto);

}
