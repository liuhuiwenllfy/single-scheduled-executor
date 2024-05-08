package cn.liulingfengyu.actuator.scheduled;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.actuator.support.MyScheduledExecutorService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
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


    /**
     * 每五秒执行一次，完成心跳检测
     */
    @Scheduled(fixedRate = 5000)
    public void heartbeatDetection() {
        if (!redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorName))){
            //任务恢复，故障转移失败（单执行器），重启需要恢复的任务
            myScheduledExecutorService.restart();
        }
        //心跳
        redisUtil.setEx(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorName), actuatorName, 7000, TimeUnit.MILLISECONDS);
        //注册
        redisUtil.hPut(RedisConstant.ACTUATOR_REGISTRY, actuatorName, actuatorName);
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
     * 每7秒执行一次，完成故障转移
     */
    @Scheduled(fixedRate = 10000)
    public void failover() {
        //获取执行器列表
        List<String> actuatorInfoList = JSONUtil.toList(JSONUtil.toJsonStr(redisUtil.hGetAll(RedisConstant.ACTUATOR_REGISTRY).values()), String.class);
        //需要转移的任务集合
        List<TaskInfo> taskInfoList = new ArrayList<>();
        //宕机的执行器列表
        Set<String> downAppName = new HashSet<>();
        //正常的执行器列表
        Set<String> normalAppName = new HashSet<>();
        //循环执行器列表
        for (String actuatorName: actuatorInfoList) {
            //验证执行器心跳
            if (Boolean.FALSE.equals(redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(actuatorName)))) {
                //添加到需要转移的任务集合中
                taskInfoList.addAll(taskInfoService.getRestartList(actuatorName));
                //添加到宕机的执行器列表
                downAppName.add(actuatorName);
            } else {
                //添加到正常的执行器列表
                normalAppName.add(actuatorName);
            }
        }
        //如果存在正常的执行器，且存在宕机的执行器，且有需要转移的任务，则进行转移操作
        if (!normalAppName.isEmpty() && !downAppName.isEmpty() && !taskInfoList.isEmpty()) {
            Random random = new Random();
            //分发任务到正常的执行器
            for (TaskInfo taskInfo : taskInfoList) {
                //随机获取一个正常的执行器
                String appName = normalAppName.toArray()[random.nextInt(normalAppName.size())].toString();
                TaskInfoBo taskInfoBo = new TaskInfoBo();
                BeanUtils.copyProperties(taskInfo, taskInfoBo);
                taskInfoBo.setAppName(appName);
                taskInfoBo.setIncident(IncidentEnum.START.getCode());
                rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
            }
        }
        if (!downAppName.isEmpty()) {
            //删除宕机的执行器任务集合
            QueryWrapper<TaskInfo> taskInfoQueryWrapper = new QueryWrapper<>();
            taskInfoQueryWrapper.in(TaskInfo.APP_NAME, downAppName);
            taskInfoService.remove(taskInfoQueryWrapper);
            log.info("执行器->{}宕机，任务已经转移到了->{}执行器", String.join(",", downAppName), String.join(",", normalAppName));
        }
    }
}