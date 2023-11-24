package com.llfy.cesea.scheduledExecutor.controller;

import com.llfy.cesea.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.scheduledExecutor.service.IScheduledExecutorService;
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

    public TaskInfoController(IScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
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
