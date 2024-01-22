package cn.liulingfengyu.actuator.scheduledExecutor.service.runnable;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.actuator.utils.CronUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.name}")
    private String appName;

    @Value("${persistence}")
    private boolean persistence;

    public BaseRunnable() {
    }

    public BaseRunnable(BaseScheduledExecutorService base, String id) {
        this.id = id;
        this.base = base;
    }

    @Override
    public void run() {
        //任务信息
        TaskInfo taskInfo = JSONUtil.toBean(base.redisUtil.hGet(appName, id).toString(), TaskInfo.class);
        //日志id
        String schedulingLogId = null;
        if (taskInfo != null) {
            //验证时间是否过期
            if (CronUtils.isExpired(taskInfo.getCron())) {
                //手动变更缓存池中任务完成状态（已完成）
                taskInfo.setDone(true);
                taskInfo.setCancelled(true);
            } else {
                //获取下一执行时间
                long nextTimeDelayMilliseconds = CronUtils.getNextTimeDelayMilliseconds(taskInfo.getCron());
                if (nextTimeDelayMilliseconds != -1) {
                    //回填下一次执行时间
                    taskInfo.setNextExecutionTime(System.currentTimeMillis() + nextTimeDelayMilliseconds);
                    //再次启动任务
                    base.startOnce(taskInfo, null);
                } else {
                    //手动变更缓存池中任务完成状态（已完成）
                    taskInfo.setCancelled(true);
                }
                //验证是否持久化到数据库
                if (persistence) {
                    schedulingLogId = base.schedulingLogService.insertItem(taskInfo);
                }
                //推送执行消息
                log.info("执行任务id->{}", id);
                base.sendMessage(IncidentEnum.CARRY_OUT.getCode(), BaseScheduledExecutorService.getTaskInfoBo(taskInfo), "执行成功");
            }
            base.redisUtil.hPut(appName, id, JSONUtil.toJsonStr(taskInfo));
            if (persistence) {
                base.taskInfoService.saveItem(taskInfo);
                taskInfo.setDone(true);
                base.schedulingLogService.updateItem(schedulingLogId, taskInfo);
            }
        }
    }
}
