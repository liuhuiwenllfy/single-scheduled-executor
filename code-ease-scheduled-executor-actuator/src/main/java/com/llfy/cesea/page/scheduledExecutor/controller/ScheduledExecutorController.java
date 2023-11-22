package com.llfy.cesea.page.scheduledExecutor.controller;

import cn.hutool.json.JSONObject;
import com.llfy.cesea.core.config.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.page.scheduledExecutor.service.IScheduledExecutorService;
import com.llfy.cesea.utils.RespJson;
import org.springframework.web.bind.annotation.*;

/**
 * 任务管理控制器
 *
 * @author 刘凌枫羽工作室
 */
@RestController
@RequestMapping("scheduledExecutor")
public class ScheduledExecutorController {

    private final IScheduledExecutorService scheduledExecutorService;

    public ScheduledExecutorController(IScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    /**
     * 启动单次任务
     *
     * @param taskDto 入参
     */
    @PostMapping("startOnce")
    public void startOnce(@RequestBody TaskDto taskDto) {
        scheduledExecutorService.startOnce(taskDto);
    }

    /**
     * 启动循环任务
     *
     * @param taskDto 入参
     */
    @PostMapping("startLoop")
    public void startLoop(@RequestBody TaskDto taskDto) {
        scheduledExecutorService.startLoop(taskDto);
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

    /**
     * 测试任务请求
     *
     * @param jsonObject 信息
     * @return {@link RespJson}
     */
    @PostMapping("callback")
    public String getThirdPartyPostInfo(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);
        return "success";
    }
}
