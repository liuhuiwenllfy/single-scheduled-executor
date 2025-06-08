package cn.liulingfengyu.actuator.controller;

import cn.liulingfengyu.actuator.dto.TaskInfoPageDto;
import cn.liulingfengyu.actuator.dto.TaskInsertDto;
import cn.liulingfengyu.actuator.dto.TaskUpdateDto;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.actuator.vo.TaskInfoVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.tools.CronUtils;
import cn.liulingfengyu.tools.exception.ErrorCode;
import cn.liulingfengyu.tools.exception.MyException;
import cn.liulingfengyu.tools.exception.RespJson;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 任务基表 前端控制器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/scheduledExecutor/taskInfo")
@Tag(name = "任务管理")
@AllArgsConstructor
public class TaskInfoController {

    private final ITaskInfoService taskInfoService;

    /**
     * 条件分页查询任务
     *
     * @param taskInfoPageDto 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    @Operation(summary = "条件分页查询任务")
    public RespJson<IPage<TaskInfoVo>> getByPage(@Validated @ParameterObject PageInfo pageInfo, @ParameterObject TaskInfoPageDto taskInfoPageDto) {
        return RespJson.success(taskInfoService.getByPage(pageInfo, taskInfoPageDto));
    }

    /**
     * 根据任务id查询任务
     *
     * @param id 任务id
     * @return {@link RespJson}
     */
    @GetMapping("queryById")
    @Operation(summary = "根据任务id查询任务")
    public RespJson<TaskInfoVo> queryById(
            @Parameter(description = "任务id", required = true) String id) {
        return RespJson.success(taskInfoService.queryById(id));
    }

    /**
     * 创建任务
     *
     * @param taskInsertDto 入参
     */
    @PostMapping("insert")
    @Operation(summary = "创建任务")
    public RespJson<Boolean> insertItem(@Validated @RequestBody TaskInsertDto taskInsertDto) {
        if (CronUtils.isValidCron(taskInsertDto.getCron())) {
            throw new MyException(ErrorCode.CRON_ERROR);
        }
        if (CronUtils.isExpired(taskInsertDto.getCron())) {
            throw new MyException(ErrorCode.CRON_EXPIRED);
        }
        taskInfoService.insertItem(taskInsertDto);
        return RespJson.state(true);
    }

    /**
     * 启动任务
     *
     * @param id 任务id
     */
    @PostMapping("updateStart")
    @Operation(summary = "启动任务")
    public RespJson<Boolean> start(@RequestParam String id) {
        taskInfoService.start(id);
        return RespJson.state(true);
    }

    /**
     * 修改任务
     *
     * @param taskUpdateDto 入参
     */
    @PutMapping("updateById")
    @Operation(summary = "修改任务")
    public RespJson<Boolean> updateItem(@Validated @RequestBody TaskUpdateDto taskUpdateDto) {
        if (CronUtils.isValidCron(taskUpdateDto.getCron())) {
            throw new MyException(ErrorCode.CRON_ERROR);
        }
        if (CronUtils.isExpired(taskUpdateDto.getCron())) {
            throw new MyException(ErrorCode.CRON_EXPIRED);
        }
        taskInfoService.updateItem(taskUpdateDto);
        return RespJson.state(true);
    }

    /**
     * 暂停任务
     *
     * @param id 任务id
     */
    @PutMapping("updateStop")
    @Operation(summary = "暂停任务")
    public RespJson<Boolean> stop(@RequestParam String id) {
        taskInfoService.stop(id);
        return RespJson.state(true);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     */
    @DeleteMapping("deleteById")
    @Operation(summary = "删除任务")
    public RespJson<Boolean> remove(@RequestParam String id) {
        taskInfoService.remove(id);
        return RespJson.state(true);
    }
}
