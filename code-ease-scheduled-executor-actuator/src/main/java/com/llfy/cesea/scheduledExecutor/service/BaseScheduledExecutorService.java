package com.llfy.cesea.scheduledExecutor.service;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.service.runnable.BaseRunnable;
import com.llfy.cesea.scheduledExecutor.service.runnable.FailoverRunnable;
import com.llfy.cesea.scheduledExecutor.service.runnable.HeartbeatRunnable;
import com.llfy.cesea.utils.RedisUtil;
import com.llfy.cesea.utils.RespJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
     * 执行器集群名称
     */
    public final String actuatorName;
    /**
     * 执行器名称
     */
    public final String appName;
    /**
     * 业务集群名称
     */
    public final String businessName;
    /**
     * 心跳检测周期
     */
    public final long appHeartbeatInterval;
    /**
     * 端口
     */
    public final String port;
    /**
     * redis
     */
    public final StringRedisTemplate stringRedisTemplate;
    /**
     * 持久化
     */
    public final boolean persistence;
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
    /**
     * 回调地址
     */
    private final String callback;
    /**
     * 当前执行器执行中的任务，key->任务id，value->计划对象
     */
    private final Map<String, ScheduledFuture<?>> map = new HashMap<>();

    public BaseScheduledExecutorService(RedisUtil redisUtil,
                                        StringRedisTemplate stringRedisTemplate,
                                        @Value("${scheduled.corePoolSize}") Integer corePoolSize,
                                        @Value("${scheduled.callback}") String callback,
                                        @Value("${actuator.name}") String actuatorName,
                                        @Value("${app.name}") String appName,
                                        @Value("${business.name}") String businessName,
                                        @Value("${app.heartbeat.interval}") long appHeartbeatInterval,
                                        @Value("${server.port}") String port,
                                        @Value("${persistence}") boolean persistence,
                                        ITaskInfoService taskInfoService,
                                        ISchedulingLogService schedulingLogService,
                                        IActuatorInfoService actuatorInfoService) {
        //核心线程数
        if (corePoolSize == null) {
            corePoolSize = 2;
        }
        executor = newScheduledThreadPool(corePoolSize);
        this.redisUtil = redisUtil;
        this.stringRedisTemplate = stringRedisTemplate;
        this.callback = callback;
        this.actuatorName = actuatorName;
        this.appName = appName;
        this.businessName = businessName;
        this.appHeartbeatInterval = appHeartbeatInterval;
        this.port = port;
        this.persistence = persistence;
        this.taskInfoService = taskInfoService;
        this.schedulingLogService = schedulingLogService;
        this.actuatorInfoService = actuatorInfoService;
    }

    /**
     * 心跳检测任务
     */
    public void heartbeatDetection() {
        Runnable command = new HeartbeatRunnable(this);
        executor.scheduleAtFixedRate(command, 1000, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 故障转移
     */
    public void failover() {
        Runnable command = new FailoverRunnable(this);
        executor.scheduleAtFixedRate(command, 2000, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }


    /**
     * 启动单次任务
     *
     * @param taskInfo        任务入参
     * @param millisecondDiff 延迟毫秒数（宕机重启使用）
     */
    public void startOnce(TaskInfo taskInfo, Long millisecondDiff) {
        //初始化延迟时间
        long initialDelay = taskInfo.getInitialDelay();
        if (millisecondDiff != null) {
            initialDelay = millisecondDiff;
        }
        //执行任务
        Runnable command = new BaseRunnable(this, taskInfo.getId());
        ScheduledFuture<?> scheduledFuture = executor.schedule(command, initialDelay, TimeUnit.MILLISECONDS);
        map.put(taskInfo.getId(), scheduledFuture);
        //保存任务信息
        taskInfo.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
        taskInfo.setAppName(appName);
        redisUtil.hPut(appName, taskInfo.getId(), JSON.toJSONString(taskInfo));
        //持久化
        if (persistence) {
            taskInfoService.saveItem(taskInfo);
        }
        sendMessage(IncidentEnum.START.getCode(), RespJson.success(taskInfo));
    }


    /**
     * 启动循环任务
     *
     * @param taskInfo 任务入参
     */
    public void startLoop(TaskInfo taskInfo, Long millisecondDiff) {
        //初始化延迟时间
        long initialDelay = taskInfo.getInitialDelay();
        if (millisecondDiff != null) {
            initialDelay = millisecondDiff;
        }
        //执行任务
        Runnable command = new BaseRunnable(this, taskInfo.getId());
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(command, initialDelay, taskInfo.getPeriod(), TimeUnit.MILLISECONDS);
        map.put(taskInfo.getId(), scheduledFuture);
        //保存任务信息
        taskInfo.setPeriodic(true);
        taskInfo.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
        taskInfo.setAppName(appName);
        taskInfo.setCancelled(false);
        redisUtil.hPut(appName, taskInfo.getId(), JSON.toJSONString(taskInfo));
        //持久化
        if (persistence && millisecondDiff == null) {
            taskInfoService.saveItem(taskInfo);
        }
        sendMessage(IncidentEnum.START.getCode(), RespJson.success(taskInfo));
    }

    /**
     * 停止任务
     *
     * @param taskId     任务id
     * @param forcedStop 是否强制停止
     */
    public void stop(String taskId, boolean forcedStop) {
        if (map.containsKey(taskId)) {
            try {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                scheduledFuture.cancel(forcedStop);
                TaskInfo taskInfo = JSON.parseObject(redisUtil.hGet(appName, taskId).toString(), TaskInfo.class);
                if (taskInfo != null) {
                    taskInfo.setCancelled(true);
                    redisUtil.hPut(appName, taskId, JSON.toJSONString(taskInfo));
                    //持久化
                    if (persistence) {
                        taskInfoService.saveItem(taskInfo);
                    }
                }
                sendMessage(IncidentEnum.STOP.getCode(), RespJson.success(taskInfo));
            } catch (Exception e) {
                log.error("任务->{}停止失败，错误信息->{}", taskId, e.getMessage());
                sendMessage(IncidentEnum.ERROR.getCode(), RespJson.error(taskId.concat("->").concat(e.getMessage())));
            }
        }
    }

    /**
     * 删除任务
     *
     * @param taskId     任务id
     * @param forcedStop 是否强制停止
     */
    public void remove(String taskId, boolean forcedStop) {
        if (map.containsKey(taskId)) {
            try {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                scheduledFuture.cancel(forcedStop);
                TaskInfo taskInfo = JSON.parseObject(redisUtil.hGet(appName, taskId).toString(), TaskInfo.class);
                map.remove(taskId);
                redisUtil.hDelete(appName, taskId);
                //持久化
                if (persistence) {
                    taskInfoService.deleteItem(taskId);
                }
                sendMessage(IncidentEnum.REMOVE.getCode(), RespJson.success(taskInfo));
            } catch (Exception e) {
                log.error("任务->{}删除失败，错误信息->{}", taskId, e.getMessage());
                sendMessage(IncidentEnum.ERROR.getCode(), RespJson.error(taskId.concat("->").concat(e.getMessage())));
            }
        }
    }

    /**
     * 更新任务状态
     *
     * @param taskId 任务id
     */
    public void updateStatus(String taskId) {
        if (map.containsKey(taskId)) {
            try {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                TaskInfo taskInfo = JSON.parseObject(redisUtil.hGet(appName, taskId).toString(), TaskInfo.class);
                if (taskInfo != null) {
                    taskInfo.setCancelled(scheduledFuture.isCancelled());
                    taskInfo.setCancelled(scheduledFuture.isDone());
                    redisUtil.hPut(appName, taskId, JSON.toJSONString(taskInfo));
                    //持久化
                    if (persistence) {
                        taskInfoService.saveItem(taskInfo);
                    }
                }
                sendMessage(IncidentEnum.UPDATE_STATUS.getCode(), RespJson.success(taskInfo));
            } catch (Exception e) {
                log.error("任务->{}状态更新失败，错误信息->{}", taskId, e.getMessage());
                sendMessage(IncidentEnum.UPDATE_STATUS.getCode(), RespJson.error(taskId.concat("->").concat(e.getMessage())));
            }
        }
    }

    /**
     * 更新任务状态
     *
     * @param taskId 任务id
     */
    public void update(String taskId, TaskInfo newTaskInfo) {
        if (map.containsKey(taskId)) {
            try {
                TaskInfo taskInfo = JSON.parseObject(redisUtil.hGet(appName, taskId).toString(), TaskInfo.class);
                if (taskInfo != null) {
                    taskInfo.setTitle(newTaskInfo.getTitle());
                    taskInfo.setTaskParam(newTaskInfo.getTaskParam());
                    if (taskInfo.isCancelled()) {
                        taskInfo.setPeriodic(newTaskInfo.isPeriodic());
                        taskInfo.setInitialDelay(newTaskInfo.getInitialDelay());
                        taskInfo.setPeriod(newTaskInfo.getPeriod());
                    }
                    redisUtil.hPut(appName, taskId, JSON.toJSONString(taskInfo));
                    //持久化
                    if (persistence) {
                        taskInfoService.saveItem(taskInfo);
                    }
                }
                sendMessage(IncidentEnum.UPDATE.getCode(), RespJson.success(taskInfo));
            } catch (Exception e) {
                log.error("任务->{}任务更新失败，错误信息->{}", taskId, e.getMessage());
                sendMessage(IncidentEnum.UPDATE.getCode(), RespJson.error(taskId.concat("->").concat(e.getMessage())));
            }
        }
    }


    /**
     * 重启任务
     *
     * @param taskInfo 任务信息
     */
    public void restart(TaskInfo taskInfo) {
        //获取下一次执行时间和当前相差毫秒数
        long millisecondDiff = taskInfo.getNextExecutionTime() - System.currentTimeMillis();
        //判断是否是循环任务
        if (taskInfo.isPeriodic()) {
            //任务未过期
            if (millisecondDiff > 0) {
                //根据剩余延迟时间执行任务
                startLoop(taskInfo, millisecondDiff);
            } else {
                //计算下一次执行延迟时间
                long periodMultiple = Math.abs(millisecondDiff) / taskInfo.getPeriod();
                long periodRemainder = Math.abs(millisecondDiff) % taskInfo.getPeriod();
                if (periodRemainder > 0) {
                    periodMultiple += 1;
                }
                taskInfo.setNextExecutionTime(taskInfo.getNextExecutionTime() + periodMultiple * taskInfo.getPeriod());
                millisecondDiff = taskInfo.getNextExecutionTime() - System.currentTimeMillis();
                startLoop(taskInfo, millisecondDiff);
            }
        } else {
            //未过期
            if (millisecondDiff > 0) {
                startOnce(taskInfo, millisecondDiff);
            }
        }
    }

    public void sendMessage(String incident, RespJson<Object> respJson) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>(2);
        stringObjectHashMap.put("incident", incident);
        stringObjectHashMap.put("data", respJson);
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(stringObjectHashMap), headers);
        send(restTemplate, entity);
    }

    private void send(RestTemplate restTemplate, HttpEntity<String> entity) {
        Set<String> businessNameSet = redisUtil.hKeys(businessName).stream().map(Object::toString).collect(Collectors.toSet());
        if (!businessNameSet.isEmpty()) {
            Random random = new Random();
            String businessNameItem = businessNameSet.toArray(new String[0])[random.nextInt(businessNameSet.size())];
            String ip = redisUtil.hGet(businessName, businessNameItem).toString();
            ResponseEntity<String> exchange;
            try {
                exchange = restTemplate.exchange("http://".concat(ip).concat(callback), HttpMethod.POST, entity, String.class);
                if (!"success".equals(exchange.getBody())) {
                    redisUtil.hDelete(businessName, businessNameItem);
                    log.error("业务系统节点->{}响应失败，消息已转发下一节点", businessNameItem);
                    send(restTemplate, entity);
                }
            } catch (Exception e) {
                redisUtil.hDelete(businessName, businessNameItem);
                log.error("业务系统节点->{}响应失败，消息已转发下一节点", businessNameItem);
                send(restTemplate, entity);
            }

        }
    }

}
