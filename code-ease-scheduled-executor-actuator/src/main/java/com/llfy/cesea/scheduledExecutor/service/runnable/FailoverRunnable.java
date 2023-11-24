package com.llfy.cesea.scheduledExecutor.service.runnable;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.ConstantConfiguration;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

    private BaseScheduledExecutorService base;

    public FailoverRunnable() {
    }

    public FailoverRunnable(BaseScheduledExecutorService base) {
        this.base = base;
    }

    @Override
    public void run() {
        //获取执行器列表
        Set<String> appNames = base.redisUtil.hKeys(base.actuatorName).stream().map(Object::toString).collect(Collectors.toSet());
        //需要转移的任务集合
        List<TaskInfo> taskInfoList = new ArrayList<>();
        //宕机的执行器列表
        Set<String> downAppName = new HashSet<>();
        //正常的执行器列表
        Set<String> normalAppName = new HashSet<>();
        //循环执行器列表
        for (String appName : appNames) {
            //验证执行器心跳
            if (Boolean.FALSE.equals(base.redisUtil.hasKey(base.actuatorName.concat("-").concat("heartbeat:").concat(appName)))) {
                //添加到需要转移的任务集合中
                taskInfoList.addAll(JSON.parseArray(base.redisUtil.hValues(appName).toString(), TaskInfo.class).stream().filter(taskInfo -> {
                    if (!taskInfo.isCancelled()) {
                        if (taskInfo.isPeriodic()) {
                            return true;
                        } else return !taskInfo.isDone();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList()));
                //添加到宕机的执行器列表
                downAppName.add(appName);
                //删除宕机的执行器任务集合
                base.redisUtil.hDelete(appName, taskInfoList.stream().map(TaskInfo::getId).toArray());
                //删除宕机的执行器
                base.redisUtil.hDelete(base.actuatorName, appName);
            } else {
                //添加到正常的执行器列表
                normalAppName.add(appName);
            }
        }
        if (!normalAppName.isEmpty() && !downAppName.isEmpty() && !taskInfoList.isEmpty()) {
            Random random = new Random();
            Map<String, List<TaskInfo>> transferMap = new HashMap<>();
            //分发任务到正常的执行器
            for (TaskInfo taskInfo : taskInfoList) {
                List<TaskInfo> temp = new ArrayList<>();
                //随机获取一个正常的执行器
                String appName = normalAppName.toArray(new String[0])[random.nextInt(normalAppName.size())];
                if (transferMap.containsKey(appName)) {
                    temp = transferMap.get(appName);
                }
                taskInfo.setAppName(appName);
                temp.add(taskInfo);
                transferMap.put(appName, temp);
            }
            //发布转移消息
            for (String appName : transferMap.keySet()) {
                MessageDto messageDto = new MessageDto();
                messageDto.setAppName(appName);
                messageDto.setTaskInfoList(transferMap.get(appName));
                messageDto.setIncident(IncidentEnum.TRANSFER.getCode());
                base.stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSON.toJSONString(messageDto));
            }

        }
        if (!downAppName.isEmpty()) {
            log.info("执行器->{}宕机，任务已经转移到了->{}执行器", String.join(",", downAppName), String.join(",", normalAppName));
        }
    }
}
