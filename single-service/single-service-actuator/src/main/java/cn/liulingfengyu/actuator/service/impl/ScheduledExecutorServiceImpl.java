package cn.liulingfengyu.actuator.service.impl;

import cn.hutool.core.lang.UUID;
import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.dto.TaskInsertDto;
import cn.liulingfengyu.actuator.dto.TaskUpdateDto;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.service.IScheduledExecutorService;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
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
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ITaskInfoService taskInfoService;

    @Override
    public void insertItem(TaskInsertDto taskInsertDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInsertDto, taskInfoBo);
        taskInfoBo.setId(UUID.randomUUID().toString(true));
        taskInfoBo.setCancelled(true);
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        sendMessage(taskInfoBo);
    }

    @Override
    public void start(String id) {
        TaskInfo taskInfo = taskInfoService.getById(id);
        start(taskInfo);
    }

    @Override
    public void start(TaskInfo taskInfo) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        taskInfoBo.setCancelled(false);
        taskInfoBo.setDone(false);
        sendMessage(taskInfoBo);
    }

    @Override
    public void updateItem(TaskUpdateDto taskUpdateDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskUpdateDto, taskInfoBo);
        taskInfoBo.setIncident(IncidentEnum.UPDATE.getCode());
        sendMessage(taskInfoBo);
    }

    @Override
    public void stop(String taskId) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setIncident(IncidentEnum.STOP.getCode());
        sendMessage(taskInfoBo);
    }

    @Override
    public void remove(String taskId) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setIncident(IncidentEnum.REMOVE.getCode());
        sendMessage(taskInfoBo);
    }

    private void sendMessage(TaskInfoBo taskInfoBo) {
        CallbackBo callbackBo = new CallbackBo();
        callbackBo.setUuId(UUID.randomUUID().toString(true));
        callbackBo.setTaskInfoBo(taskInfoBo);
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, "", callbackBo);
    }
}
