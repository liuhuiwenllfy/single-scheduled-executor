package com.llfy.cesea.scheduledExecutor;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时器初始化
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class ScheduledExecutorInitService {

    private final BaseScheduledExecutorService base;

    public ScheduledExecutorInitService(
            BaseScheduledExecutorService base) {
        this.base = base;
    }

    /**
     * 初始化任务
     */
    public void initializeTask() {
        //启动心跳检测
        base.heartbeatDetection();
        //故障转移检测
        base.failover();
        //获取缓存池中当前机器所有任务
        List<String> idList = new ArrayList<>();
        //获取需要启动的任务
        List<TaskInfo> taskInfoList = JSON.parseArray(base.redisUtil.hValues(base.appName).toString(), TaskInfo.class).stream().filter(taskInfo -> {
            if (!taskInfo.isCancelled()) {
                if (taskInfo.isPeriodic()) {
                    idList.add(taskInfo.getId());
                    return true;
                } else if (!taskInfo.isDone()) {
                    idList.add(taskInfo.getId());
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        List<TaskInfo> persistenceTaskInfoList = new ArrayList<>();
        if (base.persistence) {
            persistenceTaskInfoList = base.taskInfoService.getRestartListExcludeAppointTask(idList);
        }
        taskInfoList.addAll(persistenceTaskInfoList);
        for (TaskInfo taskInfo : taskInfoList) {
            base.restart(taskInfo);
        }

    }
}
