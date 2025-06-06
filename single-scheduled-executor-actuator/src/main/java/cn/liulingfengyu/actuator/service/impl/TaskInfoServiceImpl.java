package cn.liulingfengyu.actuator.service.impl;

import cn.hutool.core.lang.UUID;
import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.dto.TaskInfoPageDto;
import cn.liulingfengyu.actuator.dto.TaskInsertDto;
import cn.liulingfengyu.actuator.dto.TaskUpdateDto;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.mapper.TaskInfoMapper;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.actuator.vo.TaskInfoVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.mybatisplus.utils.PageUtils;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.tools.exception.ErrorCode;
import cn.liulingfengyu.tools.exception.MyException;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务基表 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
@DS("scheduled_task")
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements ITaskInfoService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public IPage<TaskInfoVo> getByPage(PageInfo pageInfo, TaskInfoPageDto taskInfoPageDto) {
        Page<TaskInfoVo> page = new PageUtils<TaskInfoVo>().getPage(pageInfo);
        List<TaskInfoVo> list =
                baseMapper.getByPage(page, taskInfoPageDto);
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item.getNextExecutionTime())) {
                // 将时间戳转换为Instant对象
                Instant instant = Instant.ofEpochMilli(Long.parseLong(item.getNextExecutionTime()));
                // 将Instant对象转换为Date对象
                Date date = Date.from(instant);
                // 设置日期格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                item.setNextExecutionTime(sdf.format(date));
            }

        });
        page.setRecords(list);
        return page;
    }

    @Override
    public TaskInfoVo queryById(String id) {
        return baseMapper.queryById(id);
    }

    @Override
    public List<TaskInfo> getRestartList(String actuatorName) {
        return baseMapper.getRestartList(actuatorName);
    }

    @Override
    public void insertItem(TaskInsertDto taskInsertDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInsertDto, taskInfoBo);
        taskInfoBo.setId(UUID.randomUUID().toString(true));
        taskInfoBo.setCancelled(true);
        sendMessage(taskInfoBo, IncidentEnum.START.getCode());
    }

    @Override
    public void start(String id) {
        TaskInfo taskInfo = baseMapper.selectById(id);
        if (taskInfo == null) {
            throw new MyException(ErrorCode.TASK_NOT_EXIST);
        }
        start(taskInfo);
    }

    @Override
    public void start(TaskInfo taskInfo) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInfo, taskInfoBo);
        taskInfoBo.setCancelled(false);
        taskInfoBo.setDone(false);
        sendMessage(taskInfoBo, IncidentEnum.START.getCode());
    }

    @Override
    public void updateItem(TaskUpdateDto taskUpdateDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskUpdateDto, taskInfoBo);
        sendMessage(taskInfoBo, IncidentEnum.UPDATE.getCode());
    }

    @Override
    public void stop(String taskId) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        sendMessage(taskInfoBo, IncidentEnum.STOP.getCode());
    }

    @Override
    public void remove(String taskId) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        sendMessage(taskInfoBo, IncidentEnum.REMOVE.getCode());
    }

    private void sendMessage(TaskInfoBo taskInfoBo, String incident) {
        CallbackBo callbackBo = new CallbackBo();
        callbackBo.setUuId(UUID.randomUUID().toString(true));
        callbackBo.setTaskInfoBo(taskInfoBo);
        callbackBo.setIncident(incident);
        rabbitTemplate.convertAndSend(ActuatorBind.ACTUATOR_EXCHANGE_NAME, ActuatorBind.ACTUATOR_ROUTING_KEY, callbackBo);
    }
}
