package cn.liulingfengyu.actuator.scheduled;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.actuator.support.MyScheduledExecutorService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.ElectUtils;
import cn.liulingfengyu.redis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MyScheduledExecutorService myScheduledExecutorService;

    @Value("${actuator.name}")
    private String actuatorName;

    @Autowired
    private ITaskInfoService taskInfoService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ElectUtils electUtils;


    /**
     * 每五秒执行一次，完成心跳检测
     */
    @Scheduled(fixedRate = 5000)
    public void heartbeatDetection() {
        if (!redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorName))) {
            //任务恢复
            myScheduledExecutorService.restart();
        }
        //心跳
        redisUtil.setEx(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorName), actuatorName, 7000, TimeUnit.MILLISECONDS);
        //注册
        redisUtil.hPut(RedisConstant.ACTUATOR_REGISTRY, actuatorName, actuatorName);
        //清理注册表
        redisUtil.hGetAll(RedisConstant.ACTUATOR_REGISTRY).values().forEach(s -> {
            if (!redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat((String) s))) {
                //删除注册表
                redisUtil.hDelete(RedisConstant.ACTUATOR_REGISTRY, s);
            }
        });
        log.info("执行器{}心跳正常", actuatorName);
    }

    /**
     * 每五秒执行一次，任务恢复
     */
    @Scheduled(fixedDelay = 5000)
    public void taskRecovery() {
        //任务恢复
        myScheduledExecutorService.restart();
    }

    /**
     * 每10秒执行一次，完成故障转移
     */
    @Scheduled(fixedDelay = 10000)
    public void failover() {
        //获取执行器列表
        List<String> actuatorCollect = JSONUtil.toList(JSONUtil.toJsonStr(redisUtil.hGetAll(RedisConstant.ACTUATOR_REGISTRY).values()), String.class);
        //需要转移的任务集合
        List<TaskInfo> taskInfoList = new ArrayList<>();
        //宕机的执行器列表
        Set<String> downAppName = new HashSet<>();
        //循环执行器列表
        for (String name : actuatorCollect) {
            //验证执行器心跳
            if (!redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(name))) {
                //添加到需要转移的任务集合中
                taskInfoList.addAll(taskInfoService.getRestartList(name));
                //添加到宕机的执行器列表
                downAppName.add(name);
            }
        }
        //如果存在正常的执行器，且存在宕机的执行器，且有需要转移的任务，则进行转移操作
        if (!downAppName.isEmpty() && !taskInfoList.isEmpty()) {
            //分发任务到正常的执行器
            for (TaskInfo taskInfo : taskInfoList) {
                TaskInfoBo taskInfoBo = new TaskInfoBo();
                BeanUtils.copyProperties(taskInfo, taskInfoBo);
                String appName = electUtils.actuatorElectUtils();
                taskInfoBo.setAppName(appName);
                taskInfoBo.setIncident(IncidentEnum.START.getCode());
                rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
                log.info("执行器->{}宕机，任务已经转移到了->{}执行器", String.join(",", downAppName), appName);
            }
        }
    }
}