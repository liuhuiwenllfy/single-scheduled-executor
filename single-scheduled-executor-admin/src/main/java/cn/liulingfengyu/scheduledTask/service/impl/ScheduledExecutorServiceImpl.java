package cn.liulingfengyu.scheduledTask.service.impl;

import cn.hutool.core.lang.UUID;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import cn.liulingfengyu.scheduledTask.dto.TaskInsertDto;
import cn.liulingfengyu.scheduledTask.dto.TaskUpdateDto;
import cn.liulingfengyu.scheduledTask.entity.ActuatorInfo;
import cn.liulingfengyu.scheduledTask.entity.TaskInfo;
import cn.liulingfengyu.scheduledTask.mapper.TaskInfoMapper;
import cn.liulingfengyu.scheduledTask.service.IActuatorInfoService;
import cn.liulingfengyu.scheduledTask.service.IScheduledExecutorService;
import cn.liulingfengyu.tools.exception.MyException;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务管理实现类
 *
 * @author 刘凌枫羽工作室
 */
@Service
@DS("scheduled_task")
public class ScheduledExecutorServiceImpl implements IScheduledExecutorService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private IActuatorInfoService actuatorInfoService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void insertItem(TaskInsertDto taskInsertDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInsertDto, taskInfoBo);
        taskInfoBo.setId(UUID.randomUUID().toString(true));
        taskInfoBo.setCancelled(true);
        taskInfoBo.setAppName(getAppName());
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
    }

    @Override
    public void start(String id) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        taskInfoBo.setAppName(getAppName());
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        taskInfoBo.setCancelled(false);
        taskInfoBo.setDone(false);
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
    }

    @Override
    public void updateItem(TaskUpdateDto taskUpdateDto) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskUpdateDto.getId());
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskUpdateDto, taskInfoBo);
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.UPDATE.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
    }

    @Override
    public void stop(String taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setTitle(taskInfo.getTitle());
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.STOP.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
    }

    @Override
    public void remove(String taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setTitle(taskInfo.getTitle());
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.REMOVE.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, taskInfoBo);
    }

    private String getAppName() {
        //随机获取执行器
        Map<String, ActuatorInfo> actuatorInfoMap = actuatorInfoService.list().stream().collect(Collectors.toMap(ActuatorInfo::getActuatorName, Function.identity()));
        Random random = new Random();
        Set<String> appNames = actuatorInfoMap.keySet();
        String appName = null;
        do {
            appNames.remove(appName);
            if (appNames.isEmpty()) {
                throw new MyException("没有可用的执行器");
            }
            appName = appNames.toArray(new String[0])[random.nextInt(appNames.size())];
        } while (!redisUtil.hasKey(RedisConstant.ACTUATOR_HEARTBEAT.concat(appName)));
        return appName;
    }
}
