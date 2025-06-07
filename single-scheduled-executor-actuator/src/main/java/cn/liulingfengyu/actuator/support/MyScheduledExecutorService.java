package cn.liulingfengyu.actuator.support;

import cn.hutool.core.lang.UUID;
import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.rabbitmq.bind.CallbackBind;
import cn.liulingfengyu.rabbitmq.bind.SchedulingLogBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import cn.liulingfengyu.tools.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public final Map<String, ScheduledFuture<?>> map = new HashMap<>();
    /**
     * 计划执行程序服务
     */
    private final ScheduledExecutorService executor;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @Autowired
    private ITaskInfoService taskInfoService;

    @Value("${actuator.name}")
    public String actuatorName;

    @Autowired
    private RedisUtil redisUtil;

    public MyScheduledExecutorService(@Value("${actuator.core-pool-size}") int corePoolSize) {
        //核心线程数
        executor = new ScheduledThreadPoolExecutor(corePoolSize <= 0 ? 2 : corePoolSize);
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void actuatorHeartbeat() {
        redisUtil.set(RedisConstant.ACTUATOR_REGISTRY.concat(actuatorName), actuatorName);
        // 检查主节点是否存在，不存在则设置主节点，存在则更新主节点的过期时间
        if (!redisUtil.hasKey(RedisConstant.MASTER_NODE)) {
            redisUtil.setIfAbsentEx(RedisConstant.MASTER_NODE, actuatorName, 15, TimeUnit.SECONDS);
        } else if (actuatorName.equals(redisUtil.get(RedisConstant.MASTER_NODE))) {
            redisUtil.expire(RedisConstant.MASTER_NODE, 15, TimeUnit.SECONDS);
        }
        // 执行器心跳
        redisUtil.setEx(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorName), actuatorName, 15, TimeUnit.SECONDS);
        if (redisUtil.get(RedisConstant.MASTER_NODE).equals(actuatorName)) {
            Set<String> keys = redisUtil.keys(RedisConstant.ACTUATOR_REGISTRY.concat("*"));
            keys.forEach(key -> {
                String name = redisUtil.get(key);
                Boolean survive = redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(name));
                if (!survive) {
                    log.info("执行器->{}故障，任务转移", name);
                    restartTask(name);
                    if (!actuatorName.equals(name)) {
                        redisUtil.delete(key);
                    }
                }
            });
        }
    }

    public void restartTask(String name) {
        List<TaskInfo> restartList = taskInfoService.getRestartList(name);
        if (!restartList.isEmpty()) {
            for (TaskInfo taskInfo : restartList) {
                taskInfoService.start(taskInfo);
            }
        }
    }

    /**
     * 启动单次任务
     *
     * @param callbackBo 任务入参
     */
    public void startOnce(CallbackBo callbackBo) {
        TaskInfo taskInfo = new TaskInfo();
        BeanUtils.copyProperties(callbackBo.getTaskInfoBo(), taskInfo);
        if (!CronUtils.isExpired(taskInfo.getCron())) {
            // 延迟时间
            long initialDelay = CronUtils.getNextTimeDelayMilliseconds(taskInfo.getCron());
            // 更新数据库状态
            taskInfo.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
            taskInfo.setCancelled(false);
            taskInfo.setAppName(actuatorName);
            taskInfoService.saveOrUpdate(taskInfo);
            //执行任务
            ScheduledFuture<?> scheduledFuture = executor.schedule(() -> {
                        //任务信息
                        TaskInfo currentTask = taskInfoService.getById(taskInfo.getId());
                        if (currentTask != null) {
                            //获取下一执行时间（验证是否需要再次执行）
                            if (!CronUtils.isExpired(currentTask.getCron())) {
                                startTheNextTask(currentTask);
                            } else {
                                //任务已过期或完成
                                currentTask.setDone(true);
                                currentTask.setCancelled(true);
                                //更新任务
                                taskInfoService.updateById(currentTask);
                            }
                            //保存执行日志
                            keepLog(currentTask);
                            //推送执行消息
                            log.info("执行任务id->{}", currentTask.getId());
                            sendMessage(IncidentEnum.CARRY_OUT.getCode(), currentTask, "执行成功");
                        }
                    },
                    initialDelay,
                    TimeUnit.MILLISECONDS);
            map.put(taskInfo.getId(), scheduledFuture);
            if (IncidentEnum.START.getCode().equals(callbackBo.getIncident())) {
                sendMessage(IncidentEnum.START.getCode(), taskInfo, "启动成功");
            } else if (IncidentEnum.UPDATE.getCode().equals(callbackBo.getIncident())) {
                sendMessage(IncidentEnum.UPDATE.getCode(), taskInfo, "修改成功");
            }
        } else {
            log.error("任务->{}执行失败，cron时间格式错误或已过期->{}", taskInfo.getId(), taskInfo.getCron());
            sendMessage(IncidentEnum.ERROR.getCode(), taskInfo, "任务执行失败，cron时间格式错误或已过期");
        }

    }

    /**
     * 停止任务
     *
     * @param callbackBo 任务入参
     */
    public void stop(CallbackBo callbackBo) {
        TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
        TaskInfo taskInfo = new TaskInfo();
        BeanUtils.copyProperties(callbackBo.getTaskInfoBo(), taskInfo);
        try {
            //更新数据库状态
            taskInfo.setCancelled(true);
            if (taskInfoService.updateById(taskInfo)) {
                //停止定时器中任务
                if (map.containsKey(taskInfo.getId())) {
                    ScheduledFuture<?> scheduledFuture = map.get(taskInfo.getId());
                    scheduledFuture.cancel(taskInfoBo.isForcedStop());
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
     * @param callbackBo 任务入参
     */
    public void remove(CallbackBo callbackBo) {
        TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
        TaskInfo taskInfo = new TaskInfo();
        BeanUtils.copyProperties(callbackBo.getTaskInfoBo(), taskInfo);
        try {
            if (taskInfoService.removeById(taskInfo.getId())) {
                //删除定时器中任务
                if (map.containsKey(taskInfo.getId())) {
                    ScheduledFuture<?> scheduledFuture = map.get(taskInfo.getId());
                    scheduledFuture.cancel(taskInfoBo.isForcedStop());
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
     * @param callbackBo 任务入参
     */
    public void update(CallbackBo callbackBo) {
        TaskInfo taskInfo = new TaskInfo();
        BeanUtils.copyProperties(callbackBo.getTaskInfoBo(), taskInfo);
        try {
            ScheduledFuture<?> scheduledFuture = map.get(taskInfo.getId());
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            startOnce(callbackBo);
        } catch (Exception e) {
            log.error("任务->{}修改失败，错误信息->{}", taskInfo.getId(), e.getMessage());
            sendMessage(IncidentEnum.UPDATE.getCode(), taskInfo, "修改失败");
        }
    }

    public void startTheNextTask(TaskInfo currentTask) {
        CallbackBo callbackBo = getCallbackBo(currentTask, IncidentEnum.CYCLE_START.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, callbackBo);
    }

    public void keepLog(TaskInfo currentTask) {
        CallbackBo callbackBo = getCallbackBo(currentTask, IncidentEnum.CYCLE_START.getCode());
        rabbitTemplate.convertAndSend(SchedulingLogBind.SCHEDULING_LOG_EXCHANGE_NAME, SchedulingLogBind.SCHEDULING_LOG_ROUTING_KEY, callbackBo);
    }

    private void sendMessage(String incident, TaskInfo taskInfo, String errorMsg) {
        CallbackBo callbackBo = getCallbackBo(taskInfo, incident);
        callbackBo.setErrorMsg(errorMsg);
        rabbitTemplate.convertAndSend(CallbackBind.CALLBACK_EXCHANGE_NAME, CallbackBind.CALLBACK_ROUTING_KEY, callbackBo);
    }

    private static CallbackBo getCallbackBo(TaskInfo currentTask, String CYCLE_START) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(currentTask, taskInfoBo);
        CallbackBo callbackBo = new CallbackBo();
        callbackBo.setUuId(UUID.randomUUID().toString(true));
        callbackBo.setIncident(CYCLE_START);
        callbackBo.setTaskInfoBo(taskInfoBo);
        return callbackBo;
    }
}
