package com.llfy.cesea.scheduledExecutor.service;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.scheduledExecutor.dto.TaskDto;
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
     * 计划执行程序服务
     */
    private final ScheduledExecutorService executor;

    /**
     * 回调地址
     */
    private final String callback;

    /**
     * redis
     */
    private final RedisUtil redisUtil;

    /**
     * 执行器集群名称
     */
    private final String actuatorName;

    /**
     * 执行器名称
     */
    private final String appName;

    /**
     * 业务集群名称
     */
    private final String businessName;

    /**
     * 心跳检测周期
     */
    private final long appHeartbeatInterval;

    /**
     * 端口
     */
    private final String port;

    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 当前执行器执行中的任务，key->任务id，value->计划对象
     */
    private final Map<String, ScheduledFuture<?>> map = new HashMap<>();

    public BaseScheduledExecutorService(RedisUtil redisUtil,
                                        @Value("${scheduled.corePoolSize}") Integer corePoolSize,
                                        @Value("${scheduled.callback}") String callback,
                                        @Value("${actuator.name}") String actuatorName,
                                        @Value("${app.name}") String appName,
                                        @Value("${business.name}") String businessName,
                                        @Value("${app.heartbeat.interval}") long appHeartbeatInterval,
                                        @Value("${server.port}") String port,
                                        StringRedisTemplate stringRedisTemplate) {
        //核心线程数
        if (corePoolSize == null) {
            corePoolSize = 2;
        }
        executor = newScheduledThreadPool(corePoolSize);
        this.callback = callback;
        this.redisUtil = redisUtil;
        this.actuatorName = actuatorName;
        this.appName = appName;
        this.businessName = businessName;
        this.appHeartbeatInterval = appHeartbeatInterval;
        this.port = port;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 心跳检测任务
     */
    public void heartbeatDetection() {
        Runnable command = new HeartbeatRunnable(actuatorName, appName, appHeartbeatInterval, redisUtil, port);
        executor.scheduleAtFixedRate(command, 1000, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 故障转移
     */
    public void failover() {
        Runnable command = new FailoverRunnable(actuatorName, appName, appHeartbeatInterval, redisUtil, this, stringRedisTemplate);
        executor.scheduleAtFixedRate(command, 2000, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }


    /**
     * 启动单次任务
     *
     * @param taskDto         任务入参
     * @param millisecondDiff 延迟毫秒数（宕机重启使用）
     */
    public void startOnce(TaskDto taskDto, long millisecondDiff) {
        //初始化延迟时间
        long initialDelay = taskDto.getInitialDelay();
        if (millisecondDiff > 0) {
            initialDelay = millisecondDiff;
        }
        //执行任务
        Runnable command = new BaseRunnable(appName, taskDto, redisUtil, this);
        ScheduledFuture<?> scheduledFuture = executor.schedule(command, initialDelay, TimeUnit.MILLISECONDS);
        map.put(taskDto.getTaskId(), scheduledFuture);
        //保存任务信息
        ScheduledFutureDto scheduledFutureDto = new ScheduledFutureDto();
        scheduledFutureDto.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
        redisUtil.hPut(appName, taskDto.getTaskId(), JSON.toJSONString(scheduledFutureDto));
        sendMessage(IncidentEnum.START.getCode(), RespJson.success(taskDto));
    }


    /**
     * 启动循环任务
     *
     * @param taskDto 任务入参
     */
    public void startLoop(TaskDto taskDto, long millisecondDiff) {
        //初始化延迟时间
        long initialDelay = taskDto.getInitialDelay();
        if (millisecondDiff > 0) {
            initialDelay = millisecondDiff;
        }
        //执行任务
        Runnable command = new BaseRunnable(appName, taskDto, redisUtil, this);
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(command, initialDelay, taskDto.getPeriod(), TimeUnit.MILLISECONDS);
        map.put(taskDto.getTaskId(), scheduledFuture);
        //保存任务信息
        ScheduledFutureDto scheduledFutureDto = new ScheduledFutureDto();
        scheduledFutureDto.setPeriodic(true);
        scheduledFutureDto.setTaskDto(taskDto);
        scheduledFutureDto.setNextExecutionTime(System.currentTimeMillis() + initialDelay);
        redisUtil.hPut(appName, scheduledFutureDto.getTaskDto().getTaskId(), JSON.toJSONString(scheduledFutureDto));
        sendMessage(IncidentEnum.START.getCode(), RespJson.success(taskDto));
    }

    /**
     * 停止任务
     *
     * @param taskId     任务id
     * @param forcedStop 是否强制停止
     */
    public void stop(String taskId, boolean forcedStop) {
        if (map.containsKey(taskId)) {
            boolean flag;
            try {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                scheduledFuture.cancel(forcedStop);
                ScheduledFutureDto scheduledFutureDto = JSON.parseObject(redisUtil.hGet(appName, taskId).toString(), ScheduledFutureDto.class);
                if (scheduledFutureDto != null) {
                    scheduledFutureDto.setCancelled(true);
                    redisUtil.hPut(appName, taskId, JSON.toJSONString(scheduledFutureDto));
                }
                flag = true;
            } catch (Exception e) {
                log.error("任务->{}停止失败，错误信息->{}", taskId, e.getMessage());
                flag = false;
            }
            sendMessage(IncidentEnum.STOP.getCode(), RespJson.state(flag));
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
            boolean flag;
            try {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                scheduledFuture.cancel(forcedStop);
                map.remove(taskId);
                redisUtil.hDelete(appName, taskId);
                flag = true;
            } catch (Exception e) {
                log.error("任务->{}删除失败，错误信息->{}", taskId, e.getMessage());
                flag = false;
            }
            sendMessage(IncidentEnum.STOP.getCode(), RespJson.state(flag));
        }
    }

    /**
     * 更新任务状态
     *
     * @param taskId 任务id
     */
    public void updateStatus(String taskId) {
        if (map.containsKey(taskId)) {
            boolean flag;
            try {
                ScheduledFuture<?> scheduledFuture = map.get(taskId);
                ScheduledFutureDto scheduledFutureDto = JSON.parseObject(redisUtil.hGet(appName, taskId).toString(), ScheduledFutureDto.class);
                if (scheduledFutureDto != null) {
                    scheduledFutureDto.setCancelled(scheduledFuture.isCancelled());
                    scheduledFutureDto.setCancelled(scheduledFuture.isDone());
                    redisUtil.hPut(appName, taskId, JSON.toJSONString(scheduledFutureDto));
                }
                flag = true;
            } catch (Exception e) {
                log.error("任务->{}状态更新失败，错误信息->{}", taskId, e.getMessage());
                flag = false;
            }
            sendMessage(IncidentEnum.STOP.getCode(), RespJson.state(flag));
        }
    }

    /**
     * 重启任务
     *
     * @param scheduledFutureDto 任务信息
     */
    public void restart(ScheduledFutureDto scheduledFutureDto) {
        //过滤掉已取消的任务
        if (!scheduledFutureDto.isCancelled()) {
            //获取下一次执行时间和当前相差毫秒数
            long millisecondDiff = scheduledFutureDto.getNextExecutionTime() - System.currentTimeMillis();
            //判断是否是循环任务
            if (scheduledFutureDto.isPeriodic()) {
                //任务未过期
                if (millisecondDiff > 0) {
                    //根据剩余延迟时间执行任务
                    startLoop(scheduledFutureDto.getTaskDto(), millisecondDiff);
                } else {
                    //计算下一次执行延迟时间
                    long periodMultiple = Math.abs(millisecondDiff) / scheduledFutureDto.getTaskDto().getPeriod();
                    long periodRemainder = Math.abs(millisecondDiff) % scheduledFutureDto.getTaskDto().getPeriod();
                    if (periodRemainder > 0) {
                        periodMultiple += 1;
                    }
                    scheduledFutureDto.setNextExecutionTime(scheduledFutureDto.getNextExecutionTime() + periodMultiple * scheduledFutureDto.getTaskDto().getPeriod());
                    millisecondDiff = scheduledFutureDto.getNextExecutionTime() - System.currentTimeMillis();
                    startLoop(scheduledFutureDto.getTaskDto(), millisecondDiff);
                }
            } else {
                //任务未完成，且未过期
                if (!scheduledFutureDto.isDone() && millisecondDiff > 0) {
                    startOnce(scheduledFutureDto.getTaskDto(), millisecondDiff);
                }
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

    private void send(RestTemplate restTemplate, HttpEntity<String> entity){
        Set<String> businessNameSet = redisUtil.hKeys(businessName).stream().map(Object::toString).collect(Collectors.toSet());
        if (!businessNameSet.isEmpty()){
            Random random = new Random();
            String businessNameItem = businessNameSet.toArray(new String[0])[random.nextInt(businessNameSet.size())];
            String ip = redisUtil.hGet(businessName, businessNameItem).toString();
            ResponseEntity<String> exchange;
            try {
                exchange = restTemplate.exchange("http://".concat(ip).concat(callback), HttpMethod.POST, entity, String.class);
                if (!"success".equals(exchange.getBody())){
                    redisUtil.hDelete(businessName, businessNameItem);
                    log.error("业务系统节点->{}响应失败，消息已转发下一节点", businessNameItem);
                    send(restTemplate, entity);
                }
            }catch (Exception e){
                redisUtil.hDelete(businessName, businessNameItem);
                log.error("业务系统节点->{}响应失败，消息已转发下一节点", businessNameItem);
                send(restTemplate, entity);
            }

        }
    }

}
