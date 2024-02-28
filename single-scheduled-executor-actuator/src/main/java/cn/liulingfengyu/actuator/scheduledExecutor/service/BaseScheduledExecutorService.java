package cn.liulingfengyu.actuator.scheduledExecutor.service;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.runnable.BaseRunnable;
import cn.liulingfengyu.actuator.scheduledExecutor.service.runnable.FailoverRunnable;
import cn.liulingfengyu.actuator.scheduledExecutor.service.runnable.HeartbeatRunnable;
import cn.liulingfengyu.actuator.utils.CronUtils;
import cn.liulingfengyu.redis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
     * redis
     */
    public final StringRedisTemplate stringRedisTemplate;

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
     * 当前执行器执行中的任务，key->任务id，value->计划对象
     */
    private final Map<String, ScheduledFuture<?>> map = new HashMap<>();
    @Value("${app.heartbeat.interval}")
    public int appHeartbeatInterval;
    @Value("${app.name}")
    public String appName;
    @Value("${persistence}")
    public boolean persistence;
    @Value("${business.name}")
    public String businessName;
    @Value("${actuator.name}")
    public String actuatorName;
    @Value("${app.address}")
    public String appAddress;
    @Value("${scheduled.corePoolSize}")
    private int corePoolSize;
    @Value("${scheduled.callback}")
    private String callback;

    public BaseScheduledExecutorService(RedisUtil redisUtil,
                                        StringRedisTemplate stringRedisTemplate,
                                        ITaskInfoService taskInfoService,
                                        ISchedulingLogService schedulingLogService,
                                        IActuatorInfoService actuatorInfoService) {
        //核心线程数
        executor = newScheduledThreadPool(corePoolSize);
        this.redisUtil = redisUtil;
        this.stringRedisTemplate = stringRedisTemplate;
        this.taskInfoService = taskInfoService;
        this.schedulingLogService = schedulingLogService;
        this.actuatorInfoService = actuatorInfoService;
    }

    public static TaskInfoBo getTaskInfoBo(TaskInfo taskInfo) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        return taskInfoBo;
    }

    /**
     * 心跳检测任务
     */
    public void heartbeatDetection() {
        Runnable command = new HeartbeatRunnable(this);
        executor.scheduleWithFixedDelay(command, 0, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 故障转移
     */
    public void failover() {
        Runnable command = new FailoverRunnable(this);
        executor.scheduleAtFixedRate(command, 0, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 任务重启
     */
    public void restart(boolean checkingTaskStatus) {
        //获取缓存池中当前机器所有任务
        List<String> idList = new ArrayList<>();
        //获取需要启动的任务
        List<TaskInfo> taskInfoList = JSONUtil.toList(redisUtil.hValues(appName).toString(), TaskInfo.class).stream().filter(taskInfo -> {
            if (!taskInfo.isCancelled()) {
                idList.add(taskInfo.getId());
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        List<TaskInfo> persistenceTaskInfoList = new ArrayList<>();
        if (persistence) {
            persistenceTaskInfoList = taskInfoService.getRestartListExcludeAppointTask(idList);
        }
        taskInfoList.addAll(persistenceTaskInfoList);
        for (TaskInfo taskInfo : taskInfoList) {
            if (checkingTaskStatus) {
                if (checkingTaskStatus(taskInfo.getId())) {
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
     * @param taskInfo 任务入参
     */
    public void startOnce(TaskInfo taskInfo, String incidentEnum) {
        //延迟时间
        long initialDelay = CronUtils.getNextTimeDelayMilliseconds(taskInfo.getCron());
        if (initialDelay != -1) {
            //保存任务信息
            taskInfo.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
            taskInfo.setAppName(appName);
            taskInfo.setCancelled(false);
            redisUtil.hPut(appName, taskInfo.getId(), JSONUtil.toJsonStr(taskInfo));
            //持久化
            if (persistence) {
                taskInfoService.saveItem(taskInfo);
            }
            //执行任务
            if (!taskInfo.isCancelled()) {
                Runnable command = new BaseRunnable(this, taskInfo.getId());
                if (!map.containsKey(taskInfo.getId()) || map.get(taskInfo.getId()).isCancelled() || map.get(taskInfo.getId()).isDone()) {
                    ScheduledFuture<?> scheduledFuture = executor.schedule(command, initialDelay, TimeUnit.MILLISECONDS);
                    map.put(taskInfo.getId(), scheduledFuture);
                }
            }
            if (IncidentEnum.START.getCode().equals(incidentEnum)) {
                sendMessage(IncidentEnum.START.getCode(), getTaskInfoBo(taskInfo), "启动成功");
            }
        } else {
            log.error("任务->{}执行失败，cron时间格式错误->{}", taskInfo.getId(), taskInfo.getCron());
            sendMessage(IncidentEnum.ERROR.getCode(), getTaskInfoBo(taskInfo), "任务执行失败，cron时间格式错误");
        }

    }

    /**
     * 停止任务
     *
     * @param taskId     任务id
     * @param forcedStop 是否强制停止
     */
    public void stop(String taskId, boolean forcedStop) {
        TaskInfo taskInfo = new TaskInfo();
        try {
            //停止定时器中任务
            if (map.containsKey(taskId)) {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                scheduledFuture.cancel(forcedStop);
            }
            //更新redis中任务状态
            taskInfo = JSONUtil.toBean(redisUtil.hGet(appName, taskId).toString(), TaskInfo.class);
            if (taskInfo != null) {
                taskInfo.setCancelled(true);
                redisUtil.hPut(appName, taskId, JSONUtil.toJsonStr(taskInfo));
            }
            //更新数据库中任务状态
            if (persistence) {
                if (taskInfo == null) {
                    taskInfo = taskInfoService.getById(taskId);
                    taskInfo.setCancelled(true);
                }
                taskInfoService.saveItem(taskInfo);
            }
            //发布停止任务消息
            if (taskInfo != null) {
                sendMessage(IncidentEnum.STOP.getCode(), getTaskInfoBo(taskInfo), "停止成功");
            }
        } catch (Exception e) {
            log.error("任务->{}停止失败，错误信息->{}", taskId, e.getMessage());
            sendMessage(IncidentEnum.ERROR.getCode(), getTaskInfoBo(taskInfo), "停止失败");
        }
    }

    /**
     * 删除任务
     *
     * @param taskId     任务id
     * @param forcedStop 是否强制停止
     */
    public void remove(String taskId, boolean forcedStop) {
        TaskInfo taskInfo = new TaskInfo();
        try {
            //删除定时器中任务
            if (map.containsKey(taskId)) {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                scheduledFuture.cancel(forcedStop);
                map.remove(taskId);
            }
            //删除redis中任务
            taskInfo = JSONUtil.toBean(redisUtil.hGet(appName, taskId).toString(), TaskInfo.class);
            if (taskInfo != null) {
                redisUtil.hDelete(appName, taskId);
            }
            //删除数据库中任务
            if (persistence) {
                taskInfoService.deleteItem(taskId);
            }
            //发布删除任务消息
            if (taskInfo != null) {
                sendMessage(IncidentEnum.REMOVE.getCode(), getTaskInfoBo(taskInfo), "删除成功");
            }
        } catch (Exception e) {
            log.error("任务->{}删除失败，错误信息->{}", taskId, e.getMessage());
            sendMessage(IncidentEnum.ERROR.getCode(), getTaskInfoBo(taskInfo), "删除失败");
        }
    }

    /**
     * 修改
     *
     * @param newTaskInfo 任务信息
     */
    public void update(TaskInfo newTaskInfo) {
        try {
            //修改redis中任务
            TaskInfo taskInfo = JSONUtil.toBean(redisUtil.hGet(appName, newTaskInfo.getId()).toString(), TaskInfo.class);
            if (taskInfo != null) {
                taskInfo.setCode(newTaskInfo.getCode());
                taskInfo.setTitle(newTaskInfo.getTitle());
                taskInfo.setCron(newTaskInfo.getCron());
                taskInfo.setTaskParam(newTaskInfo.getTaskParam());
                redisUtil.hPut(appName, newTaskInfo.getId(), JSONUtil.toJsonStr(taskInfo));
            }
            //更新数据库中任务
            if (persistence) {
                if (taskInfo == null) {
                    taskInfo = taskInfoService.getById(newTaskInfo.getId());
                    taskInfo.setCode(newTaskInfo.getCode());
                    taskInfo.setTitle(newTaskInfo.getTitle());
                    taskInfo.setCron(newTaskInfo.getCron());
                    taskInfo.setTaskParam(newTaskInfo.getTaskParam());
                }
                taskInfoService.saveItem(taskInfo);
            }
            sendMessage(IncidentEnum.UPDATE.getCode(), getTaskInfoBo(newTaskInfo), "修改成功");
        } catch (Exception e) {
            log.error("任务->{}修改失败，错误信息->{}", newTaskInfo.getId(), e.getMessage());
            sendMessage(IncidentEnum.UPDATE.getCode(), getTaskInfoBo(newTaskInfo), "修改失败");
        }
    }

    public void sendMessage(String incident, TaskInfoBo taskInfoBo, String errorMsg) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CallbackBo callbackBo = new CallbackBo();
        callbackBo.setIncident(incident);
        callbackBo.setTaskInfoBo(taskInfoBo);
        callbackBo.setErrorMsg(errorMsg);
        HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(callbackBo), headers);
        send(restTemplate, entity);
    }

    private void send(RestTemplate restTemplate, HttpEntity<String> entity) {
        Set<String> businessNameSet = redisUtil.hKeys(businessName).stream().map(Object::toString).collect(Collectors.toSet());
        if (!businessNameSet.isEmpty()) {
            Random random = new Random();
            String businessNameItem = businessNameSet.toArray(new String[0])[random.nextInt(businessNameSet.size())];
            String address = redisUtil.hGet(businessName, businessNameItem).toString();
            ResponseEntity<String> exchange;
            try {
                exchange = restTemplate.exchange(address.concat(callback), HttpMethod.POST, entity, String.class);
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

    public boolean checkingTaskStatus(String id) {
        //检查任务是否在执行中
        Set<String> appNames = redisUtil.hKeys(actuatorName).stream().map(Object::toString).collect(Collectors.toSet());
        boolean flag = false;
        for (String item : appNames) {
            if (redisUtil.hExists(item, id)) {
                TaskInfo ordTaskInfo = JSONUtil.toBean(redisUtil.hGet(item, id).toString(), TaskInfo.class);
                if (!ordTaskInfo.isCancelled()) {
                    flag = true;
                    break;
                }
            }
        }
        return !flag;
    }
}
