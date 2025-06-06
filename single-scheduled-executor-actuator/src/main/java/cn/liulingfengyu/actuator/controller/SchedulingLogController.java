package cn.liulingfengyu.actuator.controller;

import cn.liulingfengyu.actuator.dto.SchedulingLogDto;
import cn.liulingfengyu.actuator.service.ISchedulingLogService;
import cn.liulingfengyu.actuator.vo.SchedulingLogVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.tools.exception.RespJson;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "调度日志管理")
@AllArgsConstructor
public class SchedulingLogController {

    private final ISchedulingLogService schedulingLogService;

    /**
     * 条件分页查询日志
     *
     * @param pageInfo 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    @Operation(summary = "条件分页查询日志")
    public RespJson<IPage<SchedulingLogVo>> getByPage(@ParameterObject PageInfo pageInfo, @ParameterObject SchedulingLogDto schedulingLogDto) {
        return RespJson.success(schedulingLogService.getByPage(pageInfo, schedulingLogDto));
    }
}
