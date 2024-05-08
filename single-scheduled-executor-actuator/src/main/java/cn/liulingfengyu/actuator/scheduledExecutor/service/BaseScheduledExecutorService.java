package cn.liulingfengyu.actuator.scheduledExecutor.service;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.property.ActuatorProperty;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.runnable.BaseRunnable;
import cn.liulingfengyu.actuator.scheduledExecutor.service.runnable.FailoverRunnable;
import cn.liulingfengyu.actuator.scheduledExecutor.service.runnable.HeartbeatRunnable;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.utils.RedisUtil;
import cn.liulingfengyu.tools.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * 调度执行器服务
 *
 * @author 刘凌枫羽工作室
 */
@Component
@Slf4j
public class BaseScheduledExecutorService {

    /**
     * redis
     */
    public final RedisUtil redisUtil;

    /**
     * 任务服务
     */
    public final ITaskInfoService taskInfoService;
    /**
     * 日志服务
     */
    public final ISchedulingLogService schedulingLogService;
    /**
     * 执行器服务
     */
    public final IActuatorInfoService actuatorInfoService;
    /**
     * 计划执行程序服务
     */
    private final ScheduledExecutorService executor;

    private final ActuatorProperty actuatorProperty;

    public final RabbitTemplate rabbitTemplate;

    /**
     * 当前执行器执行中的任务，key->任务id，value->计划对象
     */
    private final Map<String, ScheduledFuture<?>> map = new HashMap<>();

    public BaseScheduledExecutorService(RedisUtil redisUtil,
                                        ITaskInfoService taskInfoService,
                                        ISchedulingLogService schedulingLogService,
                                        IActuatorInfoService actuatorInfoService,
                                        ActuatorProperty actuatorProperty,
                                        RabbitTemplate rabbitTemplate) {
        //核心线程数
        executor = newScheduledThreadPool(actuatorProperty.getCorePoolSize());
        this.redisUtil = redisUtil;
        this.taskInfoService = taskInfoService;
        this.schedulingLogService = schedulingLogService;
        this.actuatorInfoService = actuatorInfoService;
        this.actuatorProperty = actuatorProperty;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 心跳检测任务
     */
    public void heartbeatDetection() {
        executor.scheduleWithFixedDelay(
                new HeartbeatRunnable(this, actuatorProperty),
                20000,
                actuatorProperty.getHeartbeatInterval(),
                TimeUnit.MILLISECONDS);
    }

    /**
     * 故障转移
     */
    public void failover() {
        executor.scheduleAtFixedRate(
                new FailoverRunnable(this, actuatorProperty),
                30000,
                actuatorProperty.getHeartbeatInterval(),
                TimeUnit.MILLISECONDS);
    }

    /**
     * 任务重启
     *
     * @param checkingTaskStatus 是否检查任务当前状态
     */
    public void restart(boolean checkingTaskStatus) {
        //获取当前执行器需要重启的任务
        List<TaskInfo> taskInfos = taskInfoService.getRestartList(actuatorProperty.getName());
        for (TaskInfo taskInfo : taskInfos) {
            if (checkingTaskStatus) {
                if (!map.containsKey(taskInfo.getId())) {
                    startOnce(taskInfo, null);
                }
            } else {
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
            taskInfo.setAppName(actuatorProperty.getName());
            taskInfo.setCancelled(false);
            if (taskInfoService.saveOrUpdate(taskInfo)) {
                //执行任务
                ScheduledFuture<?> scheduledFuture = executor.schedule(
                        new BaseRunnable(this, taskInfo.getId(),
                                actuatorProperty),
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
            map.remove(taskInfo.getId());
            startOnce(taskInfo, IncidentEnum.UPDATE.getCode());
        } catch (Exception e) {
            log.error("任务->{}修改失败，错误信息->{}", taskInfo.getId(), e.getMessage());
            sendMessage(IncidentEnum.UPDATE.getCode(), taskInfo, "修改失败");
        }
    }

    public void sendMessage(String incident, TaskInfo taskInfo, String errorMsg) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        CallbackBo callbackBo = new CallbackBo();
        callbackBo.setIncident(incident);
        callbackBo.setTaskInfoBo(taskInfoBo);
        callbackBo.setErrorMsg(errorMsg);
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_CALLBACK_EXCHANGE_NAME, ActuatorBind.ACTUATOR_CALLBACK_ROUTING_KEY, callbackBo);
    }
}
