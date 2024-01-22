package cn.liulingfengyu.scheduledTask.service.impl;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.redis.bo.RedisMessageBo;
import cn.liulingfengyu.redis.publish.ConstantConfiguration;
import cn.liulingfengyu.redis.utils.RedisUtil;
import cn.liulingfengyu.scheduledTask.dto.TaskInsertDto;
import cn.liulingfengyu.scheduledTask.dto.TaskUpdateDto;
import cn.liulingfengyu.scheduledTask.entity.TaskInfo;
import cn.liulingfengyu.scheduledTask.mapper.TaskInfoMapper;
import cn.liulingfengyu.scheduledTask.service.IScheduledExecutorService;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
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
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Value("${actuator.name}")
    private String actuatorName;

    @Override
    public void insertItem(TaskInsertDto taskInsertDto) {
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskInsertDto, taskInfoBo);
        taskInfoBo.setId(UUID.randomUUID().toString().replace("-", ""));
        taskInfoBo.setCancelled(true);
        taskInfoBo.setAppName(getAppName());
        taskInfoBo.setIncident(IncidentEnum.START.getCode());
        publish(taskInfoBo);
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
        publish(taskInfoBo);
    }

    @Override
    public void updateItem(TaskUpdateDto taskUpdateDto) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskUpdateDto.getId());
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        BeanUtils.copyProperties(taskUpdateDto, taskInfoBo);
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.UPDATE.getCode());
        publish(taskInfoBo);
    }

    @Override
    public void stop(String taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.STOP.getCode());
        publish(taskInfoBo);
    }

    @Override
    public void remove(String taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        TaskInfoBo taskInfoBo = new TaskInfoBo();
        taskInfoBo.setId(taskId);
        taskInfoBo.setAppName(taskInfo.getAppName());
        taskInfoBo.setIncident(IncidentEnum.REMOVE.getCode());
        publish(taskInfoBo);
    }

    private String getAppName() {
        //随机获取执行器
        Set<String> appNames = redisUtil.hKeys(actuatorName).stream().map(Object::toString).collect(Collectors.toSet());
        Random random = new Random();
        return appNames.toArray(new String[0])[random.nextInt(appNames.size())];
    }

    private void publish(TaskInfoBo taskInfoBo) {
        //redis发布消息体
        RedisMessageBo redisMessageBo = new RedisMessageBo();
        redisMessageBo.setUuid(UUID.randomUUID().toString().replace("-", ""));
        redisMessageBo.setMessage(JSONUtil.toJsonStr(taskInfoBo));
        stringRedisTemplate.convertAndSend(ConstantConfiguration.SCHEDULED_EXECUTOR, JSONUtil.toJsonStr(redisMessageBo));
    }
}
