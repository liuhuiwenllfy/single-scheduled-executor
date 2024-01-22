package cn.liulingfengyu.scheduledTask.controller;

import cn.liulingfengyu.actuator.utils.RespJson;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.SchedulingLogDto;
import cn.liulingfengyu.scheduledTask.service.ISchedulingLogService;
import cn.liulingfengyu.scheduledTask.vo.SchedulingLogVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 调度日志表 前端控制器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/scheduledExecutor/schedulingLog")
public class SchedulingLogController {

    @Autowired
    private ISchedulingLogService schedulingLogService;

    /**
     * 条件分页查询日志
     *
     * @param pageInfo 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    public RespJson<IPage<SchedulingLogVo>> getByPage(PageInfo pageInfo, SchedulingLogDto schedulingLogDto) {
        return RespJson.success(schedulingLogService.getByPage(pageInfo, schedulingLogDto));
    }
}
