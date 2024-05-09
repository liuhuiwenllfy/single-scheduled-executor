package cn.liulingfengyu.actuator.support;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.service.ISchedulingLogService;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.utils.ElectUtils;
import cn.liulingfengyu.tools.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 调度执行器服务
 *
 * @author 刘凌枫羽工作室
 */
@Component
@Slf4j
public class MyScheduledExecutorService {

    /**
     * 当前执行器执行中的任务，key->任务id，value->计划对象
     */
    private final Map<String, ScheduledFuture<?>> map = new HashMap<>();
    /**
     * 计划执行程序服务
     */
    private final ScheduledExecutorService executor;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @Autowired
    private ITaskInfoService taskInfoService;

    @Autowired
    private ISchedulingLogService schedulingLogService;

    @Value("${actuator.name}")
    private String actuatorName;

    @Autowired
    private ElectUtils electUtils;

    public MyScheduledExecutorService(@Value("${actuator.core-pool-size}") int corePoolSize) {
        //核心线程数
        executor = new ScheduledThreadPoolExecutor(corePoolSize <= 0 ? 2 : corePoolSize);
    }

    /**
     * 任务重启
     */
    public void restart() {
        //获取当前执行器需要重启的任务
        List<TaskInfo> taskInfos = taskInfoService.getRestartList(actuatorName);
        for (TaskInfo taskInfo : taskInfos) {
            if (!map.containsKey(taskInfo.getId())) {
                startOnce(taskInfo, null);
            }
        }
    }

    /**
     * 启动单次任务
     *
     * @param taskInfo     任务入参
     * @param incidentEnum 事件类型
     */
    public void startOnce(TaskInfo taskInfo, String incidentEnum) {
        //延迟时间
        long initialDelay = CronUtils.getNextTimeDelayMilliseconds(taskInfo.getCron());
        if (initialDelay != -1) {
            //保存任务信息
            taskInfo.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
            taskInfo.setAppName(actuatorName);
            taskInfo.setCancelled(false);
            if (taskInfoService.saveOrUpdate(taskInfo)) {
                //执行任务
                ScheduledFuture<?> scheduledFuture = executor.schedule(
                        () -> {
                            //任务信息
                            TaskInfo currentTask = taskInfoService.getById(taskInfo.getId());
                            if (currentTask != null) {
                                //获取下一执行时间（验证是否需要再次执行）
                                long nextTimeDelayMilliseconds = CronUtils.getNextTimeDelayMilliseconds(currentTask.getCron());
                                if (nextTimeDelayMilliseconds != -1) {
                                    //再次启动任务
                                    TaskInfoBo taskInfoBo = new TaskInfoBo();
                                    BeanUtils.copyProperties(currentTask, taskInfoBo);
                                    taskInfoBo.setAppName(currentTask.getAppName());
                                    taskInfoBo.setIncident(IncidentEnum.UPDATE.getCode());
                                    rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
                                } else {
                                    //任务已过期或完成
                                    currentTask.setDone(true);
                                    currentTask.setCancelled(true);
                                    //更新任务
                                    taskInfoService.updateById(currentTask);
                                }
                                //保存执行日志
                                schedulingLogService.insertItem(currentTask);
                                //推送执行消息
                                log.info("执行任务id->{}", currentTask.getId());
                                sendMessage(IncidentEnum.CARRY_OUT.getCode(), currentTask, "执行成功");
                            }
                        },
                        initialDelay,
                        TimeUnit.MILLISECONDS);
                map.put(taskInfo.getId(), scheduledFuture);
                if (IncidentEnum.START.getCode().equals(incidentEnum)) {
                    sendMessage(IncidentEnum.START.getCode(), taskInfo, "启动成功");
                } else if (IncidentEnum.UPDATE.getCode().equals(incidentEnum)) {
                    sendMessage(IncidentEnum.UPDATE.getCode(), taskInfo, "修改成功");
                }
            }
        } else {
            log.error("任务->{}执行失败，cron时间格式错误->{}", taskInfo.getId(), taskInfo.getCron());
            sendMessage(IncidentEnum.ERROR.getCode(), taskInfo, "任务执行失败，cron时间格式错误");
        }

    }

    /**
     * 停止任务
     *
     * @param taskInfo   任务id
     * @param forcedStop 是否强制停止
     */
    public void stop(TaskInfo taskInfo, boolean forcedStop) {
        try {
            //更新数据库状态
            taskInfo.setCancelled(true);
            if (taskInfoService.updateById(taskInfo)) {
                //停止定时器中任务
                if (map.containsKey(taskInfo.getId())) {
                    ScheduledFuture<?> scheduledFuture = map.get(taskInfo.getId());
                    scheduledFuture.cancel(forcedStop);
                }
                //发布停止任务消息
                sendMessage(IncidentEnum.STOP.getCode(), taskInfo, "停止成功");
            }
        } catch (Exception e) {
            log.error("任务->{}停止失败，错误信息->{}", taskInfo.getId(), e.getMessage());
            sendMessage(IncidentEnum.ERROR.getCode(), taskInfo, "停止失败");
        }
    }

    /**
     * 删除任务
     *
     * @param taskInfo   任务信息
     * @param forcedStop 是否强制停止
     */
    public void remove(TaskInfo taskInfo, boolean forcedStop) {
        try {
            if (taskInfoService.removeById(taskInfo.getId())) {
                //删除定时器中任务
                if (map.containsKey(taskInfo.getId())) {
                    ScheduledFuture<?> scheduledFuture = map.get(taskInfo.getId());
                    scheduledFuture.cancel(forcedStop);
                    map.remove(taskInfo.getId());
                }
                //发布删除任务消息
                sendMessage(IncidentEnum.REMOVE.getCode(), taskInfo, "删除成功");
            }
        } catch (Exception e) {
            log.error("任务->{}删除失败，错误信息->{}", taskInfo.getId(), e.getMessage());
            sendMessage(IncidentEnum.ERROR.getCode(), taskInfo, "删除失败");
        }
    }

    /**
     * 修改
     *
     * @param taskInfo 任务信息
     */
    public void update(TaskInfo taskInfo) {
        try {
            ScheduledFuture<?> scheduledFuture = map.get(taskInfo.getId());
            scheduledFuture.cancel(false);
            startOnce(taskInfo, IncidentEnum.UPDATE.getCode());
        } catch (Exception e) {
            log.error("任务->{}修改失败，错误信息->{}", taskInfo.getId(), e.getMessage());
            sendMessage(IncidentEnum.UPDATE.getCode(), taskInfo, "修改失败");
        }
    }

    private void sendMessage(String incident, TaskInfo taskInfo, String errorMsg) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        CallbackBo callbackBo = new CallbackBo();
        callbackBo.setIncident(incident);
        callbackBo.setTaskInfoBo(taskInfoBo);
        callbackBo.setErrorMsg(errorMsg);
        callbackBo.setSchedulerName(electUtils.adminElectUtils());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_CALLBACK_EXCHANGE_NAME, ActuatorBind.ACTUATOR_CALLBACK_ROUTING_KEY, callbackBo);
    }
}
