package cn.liulingfengyu.actuator.support;

import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.service.ITaskInfoService;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class MessageSubListener {

    @Autowired
    private MyScheduledExecutorService myScheduledExecutorService;

    @Value("${actuator.name}")
    private String actuatorName;

    @Autowired
    private ITaskInfoService taskInfoService;

    @RabbitListener(queues = ActuatorBind.ACTUATOR_QUEUE_NAME)
    public void onMessage(TaskInfoBo taskInfoBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (actuatorName.equals(taskInfoBo.getAppName())) {
                //启动
                if (IncidentEnum.START.getCode().equals(taskInfoBo.getIncident())) {
                    //检查任务是否在执行中
                    TaskInfo taskInfo = taskInfoService.getById(taskInfoBo.getId());
                    if (taskInfo == null || taskInfo.isCancelled()) {
                        taskInfo = new TaskInfo();
                        BeanUtils.copyProperties(taskInfoBo, taskInfo);
                        taskInfo.setCreateTime(new Date());
                        myScheduledExecutorService.startOnce(taskInfo, taskInfoBo.getIncident());
                    }
                }
                //修改
                else if (IncidentEnum.UPDATE.getCode().equals(taskInfoBo.getIncident())) {
                    TaskInfo taskInfo = new TaskInfo();
                    BeanUtils.copyProperties(taskInfoBo, taskInfo);
                    myScheduledExecutorService.update(taskInfo);
                }
                //停止
                else if (IncidentEnum.STOP.getCode().equals(taskInfoBo.getIncident())) {
                    TaskInfo taskInfo = new TaskInfo();
                    BeanUtils.copyProperties(taskInfoBo, taskInfo);
                    myScheduledExecutorService.stop(taskInfo, false);
                }
                //删除
                else if (IncidentEnum.REMOVE.getCode().equals(taskInfoBo.getIncident())) {
                    TaskInfo taskInfo = new TaskInfo();
                    BeanUtils.copyProperties(taskInfoBo, taskInfo);
                    myScheduledExecutorService.remove(taskInfo, true);
                }
                channel.basicAck(deliveryTag, false);
            }
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
        }
    }
}
