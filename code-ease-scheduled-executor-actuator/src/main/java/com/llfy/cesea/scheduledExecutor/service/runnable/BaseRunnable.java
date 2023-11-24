package com.llfy.cesea.scheduledExecutor.service.runnable;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
import com.llfy.cesea.utils.RespJson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 基础执行函数
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
@Slf4j
@Component
public class BaseRunnable implements Runnable {

    private BaseScheduledExecutorService base;

    private String id;

    public BaseRunnable() {
    }

    public BaseRunnable(BaseScheduledExecutorService base, String id) {
        this.id = id;
        this.base = base;
    }

    @Override
    public void run() {
        //手动变更缓存池中任务完成状态（未完成）
        TaskInfo taskInfo = JSON.parseObject(base.redisUtil.hGet(base.appName, id).toString(), TaskInfo.class);
        if (taskInfo != null) {
            taskInfo.setDone(false);
            base.redisUtil.hPut(base.appName, id, JSON.toJSONString(taskInfo));
            //持久化
            String schedulingLogId = null;
            if (base.persistence) {
                base.taskInfoService.saveItem(taskInfo);
                schedulingLogId = base.schedulingLogService.insertItem(base.appName, taskInfo);
            }
            log.info("执行任务id->{}", id);
            base.sendMessage(IncidentEnum.CARRY_OUT.getCode(), RespJson.success(taskInfo));
            //手动变更缓存池中任务完成状态（已完成）
            taskInfo.setDone(true);
            if (taskInfo.isPeriodic()) {
                taskInfo.setNextExecutionTime(taskInfo.getNextExecutionTime() + taskInfo.getPeriod());
            }
            base.redisUtil.hPut(base.appName, id, JSON.toJSONString(taskInfo));
            if (base.persistence) {
                base.taskInfoService.saveItem(taskInfo);
                base.schedulingLogService.updateItem(schedulingLogId, taskInfo);
            }
        }
    }
}
