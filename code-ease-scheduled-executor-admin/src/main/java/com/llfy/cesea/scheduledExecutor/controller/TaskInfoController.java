package com.llfy.cesea.scheduledExecutor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llfy.cesea.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.scheduledExecutor.dto.TaskInfoPageDto;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.service.IScheduledExecutorService;
import com.llfy.cesea.scheduledExecutor.service.ITaskInfoService;
import com.llfy.cesea.utils.RespJson;
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
public class TaskInfoController {

    private final IScheduledExecutorService scheduledExecutorService;

    private final ITaskInfoService taskInfoService;

    public TaskInfoController(IScheduledExecutorService scheduledExecutorService,
                              ITaskInfoService taskInfoService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.taskInfoService = taskInfoService;
    }

    /**
     * 条件分页查询任务
     *
     * @param taskInfoPageDto 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    public RespJson<IPage<TaskInfo>> getByPage(Page<TaskInfo> page, TaskInfoPageDto taskInfoPageDto) {
        return RespJson.success(taskInfoService.getByPage(page, taskInfoPageDto));
    }

    /**
     * 启动任务
     *
     * @param taskDto 入参
     */
    @PostMapping("start")
    public void start(@RequestBody TaskDto taskDto) {
        scheduledExecutorService.start(taskDto);
    }

    /**
     * 暂停任务
     *
     * @param taskId 任务id
     */
    @PostMapping("stop")
    public void stop(@RequestParam String taskId) {
        scheduledExecutorService.stop(taskId);
    }

    /**
     * 删除任务
     *
     * @param taskId 任务id
     */
    @PostMapping("remove")
    public void remove(String taskId) {
        scheduledExecutorService.remove(taskId);
    }

    /**
     * 更新状态
     *
     * @param taskId 任务id
     */
    @GetMapping("updateStatus")
    public void updateStatus(String taskId) {
        scheduledExecutorService.updateStatus(taskId);
    }
}
