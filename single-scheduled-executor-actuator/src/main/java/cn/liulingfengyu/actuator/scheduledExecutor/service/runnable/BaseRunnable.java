package cn.liulingfengyu.actuator.scheduledExecutor.service.runnable;

import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.property.ActuatorProperty;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.SchedulingLog;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.tools.CronUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    private ActuatorProperty actuatorProperty;

    public BaseRunnable() {
    }

    public BaseRunnable(BaseScheduledExecutorService base, String id, ActuatorProperty actuatorProperty) {
        this.id = id;
        this.base = base;
        this.actuatorProperty = actuatorProperty;
    }

    @Override
    public void run() {
        //任务信息
        TaskInfo taskInfo = base.taskInfoService.getById(id);
        //日志id
        String schedulingLogId = null;
        if (taskInfo != null) {
            //验证时间是否过期
            if (CronUtils.isExpired(taskInfo.getCron())) {
                //任务已过期或完成
                taskInfo.setDone(true);
                taskInfo.setCancelled(true);
            } else {
                //获取下一执行时间
                long nextTimeDelayMilliseconds = CronUtils.getNextTimeDelayMilliseconds(taskInfo.getCron());
                if (nextTimeDelayMilliseconds != -1) {
                    //再次启动任务
                    TaskInfoBo taskInfoBo = new TaskInfoBo();
                    BeanUtils.copyProperties(taskInfo, taskInfoBo);
                    taskInfoBo.setAppName(taskInfo.getAppName());
                    taskInfoBo.setIncident(IncidentEnum.UPDATE.getCode());
                    base.rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
                } else {
                    //任务已过期或完成
                    taskInfo.setCancelled(true);
                }
                //保存执行日志
                schedulingLogId = base.schedulingLogService.insertItem(taskInfo);
                //推送执行消息
                log.info("执行任务id->{}", id);
                base.sendMessage(IncidentEnum.CARRY_OUT.getCode(), taskInfo, "执行成功");
            }
            //更新任务
            base.taskInfoService.updateById(taskInfo);
            //完成任务更新日志
            SchedulingLog schedulingLog = new SchedulingLog();
            schedulingLog.setId(schedulingLogId);
            schedulingLog.setDone(true);
            base.schedulingLogService.updateById(schedulingLog);
        }
    }
}
