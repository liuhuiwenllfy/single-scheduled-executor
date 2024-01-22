package cn.liulingfengyu.scheduledTask.service;

import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.SchedulingLogDto;
import cn.liulingfengyu.scheduledTask.entity.SchedulingLog;
import cn.liulingfengyu.scheduledTask.vo.SchedulingLogVo;
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

    /**
     * 删除三十天前的执行器日志
     */
    void deleteThirtyDaysAgoActuatorLogs();

}
