package com.llfy.cesea.core.config.scheduledExecutor.runnable;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.config.redis.publish.ConstantConfiguration;
import com.llfy.cesea.core.config.scheduledExecutor.conf.BaseScheduledExecutorService;
import com.llfy.cesea.core.config.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.core.config.scheduledExecutor.dto.ScheduledFutureDto;
import com.llfy.cesea.core.config.scheduledExecutor.enums.IncidentEnum;
import com.llfy.cesea.utils.RedisUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 故障转移执行函数
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@Setter
@Slf4j
@Component
public class FailoverRunnable implements Runnable {

    private String appName;

    private long appHeartbeatInterval;

    private RedisUtil redisUtil;

    private BaseScheduledExecutorService baseScheduledExecutorService;

    private StringRedisTemplate stringRedisTemplate;

    public FailoverRunnable() {
    }

    public FailoverRunnable(
            String appName,
            long appHeartbeatInterval,
            RedisUtil redisUtil,
            BaseScheduledExecutorService baseScheduledExecutorService,
            StringRedisTemplate stringRedisTemplate) {
        this.appName = appName;
        this.appHeartbeatInterval = appHeartbeatInterval;
        this.redisUtil = redisUtil;
        this.baseScheduledExecutorService = baseScheduledExecutorService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run() {
        //获取执行器列表
        Set<String> appNames = redisUtil.hKeys("actuator").stream().map(Object::toString).collect(Collectors.toSet());
        //需要转移的任务集合
        List<ScheduledFutureDto> scheduledFutureDtoList = new ArrayList<>();
        //宕机的执行器列表
        Set<String> downAppName = new HashSet<>();
        //正常的执行器列表
        Set<String> normalAppName = new HashSet<>();
        //循环执行器列表
        for (String appName : appNames) {
            //验证执行器心跳
            if (Boolean.FALSE.equals(redisUtil.hasKey("actuatorHeartbeat:".concat(appName)))) {
                //添加到需要转移的任务集合中
                scheduledFutureDtoList.addAll(JSON.parseArray(redisUtil.hValues(appName).toString(), ScheduledFutureDto.class));
                //添加到宕机的执行器列表
                downAppName.add(appName);
                //删除宕机的执行器任务集合
                List<String> taskIdList = scheduledFutureDtoList.stream().map(scheduledFutureDto -> scheduledFutureDto.getTaskDto().getTaskId()).collect(Collectors.toList());
                redisUtil.hDelete(appName, taskIdList.toArray());
                //删除宕机的执行器
                redisUtil.hDelete("actuator", appName);
            } else {
                //添加到正常的执行器列表
                normalAppName.add(appName);
            }
        }
        if (!normalAppName.isEmpty()) {
            Map<String, List<ScheduledFutureDto>> transferMap = new HashMap<>();
            //分发任务到正常的执行器
            List<ScheduledFutureDto> temp = new ArrayList<>();
            for (ScheduledFutureDto scheduledFutureDto : scheduledFutureDtoList) {
                //随机获取一个正常的执行器
                String appName = normalAppName.stream().findFirst().orElse(null);
                temp.clear();
                if (transferMap.containsKey(appName)) {
                    temp = transferMap.get(appName);
                }
                temp.add(scheduledFutureDto);
                transferMap.put(appName, temp);
            }
            //发布转移消息
            for (String appName : transferMap.keySet()) {
                MessageDto messageDto = new MessageDto();
                messageDto.setAppName(appName);
                messageDto.setScheduledFutureDtoList(transferMap.get(appName));
                messageDto.setIncident(IncidentEnum.TRANSFER.getCode());
                stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
            }

        }
        if (!downAppName.isEmpty()) {
            log.info("执行器->{}宕机，任务已经转移到了->{}执行器", String.join(",", downAppName), String.join(",", normalAppName));
        }
    }
}
