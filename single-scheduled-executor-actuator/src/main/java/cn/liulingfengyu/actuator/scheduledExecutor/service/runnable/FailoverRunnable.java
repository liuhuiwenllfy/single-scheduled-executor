package cn.liulingfengyu.actuator.scheduledExecutor.service.runnable;

import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.property.ActuatorProperty;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.ActuatorInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
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

    private ActuatorProperty actuatorProperty;

    public FailoverRunnable() {
    }

    public FailoverRunnable(BaseScheduledExecutorService base,
                            ActuatorProperty actuatorProperty) {
        this.base = base;
        this.actuatorProperty = actuatorProperty;
    }

    @Override
    public void run() {
        //获取执行器列表
        Map<String, ActuatorInfo> actuatorInfoMap = base.actuatorInfoService.list().stream().collect(Collectors.toMap(ActuatorInfo::getActuatorName, Function.identity()));
        //需要转移的任务集合
        List<TaskInfo> taskInfoList = new ArrayList<>();
        //宕机的执行器列表
        Set<String> downAppName = new HashSet<>();
        //正常的执行器列表
        Set<String> normalAppName = new HashSet<>();
        //循环执行器列表
        for (String appName : actuatorInfoMap.keySet()) {
            //验证执行器心跳
            if (Boolean.FALSE.equals(base.redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(appName)))) {
                //添加到需要转移的任务集合中
                taskInfoList.addAll(base.taskInfoService.getRestartList(appName));
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
                base.rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
            }
        }
        if (!downAppName.isEmpty()) {
            //删除宕机的执行器任务集合
            QueryWrapper<TaskInfo> taskInfoQueryWrapper = new QueryWrapper<>();
            taskInfoQueryWrapper.in(TaskInfo.APP_NAME, downAppName);
            base.taskInfoService.remove(taskInfoQueryWrapper);
            //删除宕机的执行器
            QueryWrapper<ActuatorInfo> actuatorInfoQueryWrapper = new QueryWrapper<>();
            actuatorInfoQueryWrapper.in(TaskInfo.APP_NAME, downAppName);
            base.actuatorInfoService.remove(actuatorInfoQueryWrapper);
            log.info("执行器->{}宕机，任务已经转移到了->{}执行器", String.join(",", downAppName), String.join(",", normalAppName));
        }
    }
}
