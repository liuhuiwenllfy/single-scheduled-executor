package cn.liulingfengyu.scheduledTask.service.impl;

import cn.hutool.core.lang.UUID;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.redis.utils.ElectUtils;
import cn.liulingfengyu.scheduledTask.dto.TaskInsertDto;
import cn.liulingfengyu.scheduledTask.dto.TaskUpdateDto;
import cn.liulingfengyu.scheduledTask.entity.TaskInfo;
import cn.liulingfengyu.scheduledTask.mapper.TaskInfoMapper;
import cn.liulingfengyu.scheduledTask.service.IScheduledExecutorService;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务管理实现类
 *
 * @author 刘凌枫羽工作室
 */
@Service
@DS("scheduled_task")
public class ScheduledExecutorServiceImpl implements IScheduledExecutorService {

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ElectUtils electUtils;

    @Override
    public void insertItem(TaskInsertDto taskInsertDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInsertDto, taskInfoBo);
        taskInfoBo.setId(UUID.randomUUID().toString(true));
        taskInfoBo.setCancelled(true);
        taskInfoBo.setAppName(electUtils.actuatorElectUtils());
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, "", taskInfoBo);
    }

    @Override
    public void start(String id) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        taskInfoBo.setAppName(electUtils.actuatorElectUtils());
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        taskInfoBo.setCancelled(false);
        taskInfoBo.setDone(false);
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, "", taskInfoBo);
    }

    @Override
    public void updateItem(TaskUpdateDto taskUpdateDto) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskUpdateDto.getId());
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskUpdateDto, taskInfoBo);
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.UPDATE.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, "", taskInfoBo);
    }

    @Override
    public void stop(String taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setTitle(taskInfo.getTitle());
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.STOP.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, "", taskInfoBo);
    }

    @Override
    public void remove(String taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setTitle(taskInfo.getTitle());
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.REMOVE.getCode());
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, "", taskInfoBo);
    }
}
