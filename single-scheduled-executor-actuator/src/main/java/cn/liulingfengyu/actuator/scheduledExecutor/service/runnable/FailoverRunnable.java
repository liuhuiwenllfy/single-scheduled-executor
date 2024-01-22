package cn.liulingfengyu.actuator.scheduledExecutor.service.runnable;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.redis.bo.RedisMessageBo;
import cn.liulingfengyu.redis.publish.ConstantConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${actuator.name}")
    private String actuatorName;

    public FailoverRunnable() {
    }

    public FailoverRunnable(BaseScheduledExecutorService base) {
        this.base = base;
    }

    @Override
    public void run() {
        //获取执行器列表
        Set<String> appNames = base.redisUtil.hKeys(actuatorName).stream().map(Object::toString).collect(Collectors.toSet());
        //需要转移的任务集合
        List<TaskInfo> taskInfoList = new ArrayList<>();
        //宕机的执行器列表
        Set<String> downAppName = new HashSet<>();
        //正常的执行器列表
        Set<String> normalAppName = new HashSet<>();
        //循环执行器列表
        for (String appName : appNames) {
            //验证执行器心跳
            if (Boolean.FALSE.equals(base.redisUtil.hasKey(actuatorName.concat("-").concat("heartbeat:").concat(appName)))) {
                //添加到需要转移的任务集合中
                taskInfoList.addAll(JSONUtil.toList(base.redisUtil.hValues(appName).toString(), TaskInfo.class).stream().filter(taskInfo -> !taskInfo.isCancelled() && !taskInfo.isDone()).collect(Collectors.toList()));
                //添加到宕机的执行器列表
                downAppName.add(appName);
            } else {
                //添加到正常的执行器列表
                normalAppName.add(appName);
            }
        }
        //如果存在正常的执行器，且存在宕机的执行器，且有需要转移的任务，则进行转移操作
        if (!normalAppName.isEmpty() && !downAppName.isEmpty() && !taskInfoList.isEmpty()) {
            Random random = new Random();
            //分发任务到正常的执行器
            for (TaskInfo taskInfo : taskInfoList) {
                //随机获取一个正常的执行器
                String appName = normalAppName.toArray(new String[0])[random.nextInt(normalAppName.size())];
                TaskInfoBo taskInfoBo = new TaskInfoBo();
                BeanUtils.copyProperties(taskInfo, taskInfoBo);
                taskInfoBo.setAppName(appName);
                taskInfoBo.setIncident(IncidentEnum.START.getCode());
                RedisMessageBo redisMessageBo = new RedisMessageBo();
                redisMessageBo.setUuid(UUID.randomUUID().toString().replace("-", ""));
                redisMessageBo.setMessage(JSONUtil.toJsonStr(taskInfoBo));
                base.stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSONUtil.toJsonStr(redisMessageBo));
            }
        }
        if (!downAppName.isEmpty()) {
            for (String appName : appNames) {
                //删除宕机的执行器任务集合
                base.redisUtil.hDelete(appName, taskInfoList.stream().map(TaskInfo::getId).toArray());
                //删除宕机的执行器
                base.redisUtil.hDelete(actuatorName, appName);
            }
            log.info("执行器->{}宕机，任务已经转移到了->{}执行器", String.join(",", downAppName), String.join(",", normalAppName));
        }
    }
}
