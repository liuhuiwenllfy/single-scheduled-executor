package cn.liulingfengyu.scheduledTask.service.impl;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.rabbitmq.bind.ActuatorBind;
import cn.liulingfengyu.scheduledTask.enums.InterfaceEnum;
import cn.liulingfengyu.scheduledTask.service.ISchedulingLogService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * <p>
 * 回调 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
@Slf4j
public class CallbackServiceImpl {

    @Autowired
    private ISchedulingLogService schedulingLogService;

    @Value("${app.name}")
    private String appName;

    @RabbitListener(queues = ActuatorBind.ACTUATOR_CALLBACK_QUEUE_NAME)
    public void callback(CallbackBo callbackBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (appName.equals(callbackBo.getSchedulerName())) {
                String incident = callbackBo.getIncident();
                TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
                if (IncidentEnum.CARRY_OUT.getCode().equals(incident)) {
                    //删除30天前的执行器日志
                    if (taskInfoBo.getCode().equals(InterfaceEnum.DELETE_THIRTY_DAYS_AGO_ACTUATOR_LOGS.getCode())) {
                        schedulingLogService.deleteThirtyDaysAgoActuatorLogs();
                    }
                }
                log.info("任务->{}，执行器->{}，消息->{}", taskInfoBo.getTitle(), taskInfoBo.getAppName(), callbackBo.getErrorMsg());
                channel.basicAck(deliveryTag, false);
            }
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
        }
    }
}
