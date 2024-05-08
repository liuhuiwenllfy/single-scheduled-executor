package cn.liulingfengyu.actuator.conf;

import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.property.ActuatorProperty;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * 订阅监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Slf4j
@Component
@Primary
public class ScheduledExecutorRedisMessageSubListener {

    @Autowired
    private BaseScheduledExecutorService base;

    @Autowired
    private ActuatorProperty actuatorProperty;

    @RabbitListener(queues = ActuatorBind.ACTUATOR_QUEUE_NAME)
    public void onMessage(TaskInfoBo taskInfoBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (actuatorProperty.getName().equals(taskInfoBo.getAppName())) {
                //启动
                if (IncidentEnum.START.getCode().equals(taskInfoBo.getIncident())) {
                    //检查任务是否在执行中
                    TaskInfo taskInfo = base.taskInfoService.getById(taskInfoBo.getId());
                    if (taskInfo == null || base.taskInfoService.getById(taskInfoBo.getId()).isCancelled()) {
                        taskInfo = new TaskInfo();
                        BeanUtils.copyProperties(taskInfoBo, taskInfo);
                        taskInfo.setCreateTime(new Date());
                        base.startOnce(taskInfo, taskInfoBo.getIncident());
                    }
                }
                //修改
                else if (IncidentEnum.UPDATE.getCode().equals(taskInfoBo.getIncident())) {
                    TaskInfo taskInfo = new TaskInfo();
                    BeanUtils.copyProperties(taskInfoBo, taskInfo);
                    base.update(taskInfo);
                }
                //停止
                else if (IncidentEnum.STOP.getCode().equals(taskInfoBo.getIncident())) {
                    TaskInfo taskInfo = new TaskInfo();
                    BeanUtils.copyProperties(taskInfoBo, taskInfo);
                    base.stop(taskInfo, false);
                }
                //删除
                else if (IncidentEnum.REMOVE.getCode().equals(taskInfoBo.getIncident())) {
                    TaskInfo taskInfo = new TaskInfo();
                    BeanUtils.copyProperties(taskInfoBo, taskInfo);
                    base.remove(taskInfo, true);
                }
                channel.basicAck(deliveryTag, false);
            }
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
        }
    }
}
