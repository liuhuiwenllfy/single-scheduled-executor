package com.llfy.cesea.core.config.scheduledExecutor.conf;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.config.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.core.config.scheduledExecutor.dto.TaskDto;
import com.llfy.cesea.core.config.scheduledExecutor.enums.IncidentEnum;
import com.llfy.cesea.core.config.scheduledExecutor.runnable.BaseRunnable;
import com.llfy.cesea.core.config.scheduledExecutor.runnable.FailoverRunnable;
import com.llfy.cesea.core.config.scheduledExecutor.runnable.HeartbeatRunnable;
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
     * 执行器名称
     */
    private final String appName;

    /**
     * 心跳检测周期
     */
    private final long appHeartbeatInterval;

    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 当前执行器执行中的任务，key->任务id，value->计划对象
     */
    private final Map<String, ScheduledFuture<?>> map = new HashMap<>();

    public BaseScheduledExecutorService(RedisUtil redisUtil,
                                        @Value("${scheduled.corePoolSize}") Integer corePoolSize,
                                        @Value("${scheduled.callback}") String callback,
                                        @Value("${app.name}") String appName,
                                        @Value("${app.heartbeat.interval}") long appHeartbeatInterval,
                                        StringRedisTemplate stringRedisTemplate) {
        //核心线程数
        if (corePoolSize == null) {
            corePoolSize = 2;
        }
        executor = newScheduledThreadPool(corePoolSize);
        this.callback = callback;
        this.redisUtil = redisUtil;
        this.appName = appName;
        this.appHeartbeatInterval = appHeartbeatInterval;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 心跳检测任务
     */
    public void heartbeatDetection() {
        Runnable command = new HeartbeatRunnable(appName, appHeartbeatInterval, redisUtil);
        executor.scheduleAtFixedRate(command, 1000, appHeartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 故障转移
     */
    public void failover() {
        Runnable command = new FailoverRunnable(appName, appHeartbeatInterval, redisUtil, this, stringRedisTemplate);
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
        String[] callbackArray = callback.split(",");
        for (String callback : callbackArray) {
            try {
                ResponseEntity<String> exchange = restTemplate.exchange(callback, HttpMethod.POST, entity, String.class);
                if ("success".equals(exchange.getBody())) {
                    break;
                }
            } catch (Exception e) {
                log.error("{}->回调地址异常", callback);
            }
        }
    }

}
