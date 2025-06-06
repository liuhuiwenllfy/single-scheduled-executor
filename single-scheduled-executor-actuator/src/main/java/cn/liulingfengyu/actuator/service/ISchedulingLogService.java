package cn.liulingfengyu.actuator.service;

import cn.liulingfengyu.actuator.dto.SchedulingLogDto;
import cn.liulingfengyu.actuator.entity.SchedulingLog;
import cn.liulingfengyu.actuator.vo.SchedulingLogVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 调度日志表 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface ISchedulingLogService extends IService<SchedulingLog> {

    /**
     * 条件分页查询日志
     *
     * @param pageInfo 分页参数
     * @return {@link IPage}
     */
    IPage<SchedulingLogVo> getByPage(PageInfo pageInfo, SchedulingLogDto schedulingLogDto);
}
