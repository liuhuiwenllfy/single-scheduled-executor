package cn.liulingfengyu.actuator.conf;

import cn.hutool.json.JSONUtil;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.scheduledExecutor.entity.TaskInfo;
import cn.liulingfengyu.actuator.scheduledExecutor.service.BaseScheduledExecutorService;
import cn.liulingfengyu.redis.publish.ConstantConfiguration;
import cn.liulingfengyu.redis.publish.MessageSubListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 订阅监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Slf4j
@Component
@Primary
public class ScheduledExecutorRedisMessageSubListener extends MessageSubListener {

    @Autowired
    private BaseScheduledExecutorService base;

    @Value("${app.name}")
    private String appName;

    @Override
    public void onMessage() {
        String channel = super.getChannel();
        String msg = super.getMsg();
        if (channel.equals(ConstantConfiguration.SCHEDULED_EXECUTOR)) {
            log.info("redis订阅：主题->{}，消息->{}", channel, msg);
            TaskInfoBo taskInfoBo = JSONUtil.toBean(msg, TaskInfoBo.class);
            if (appName.equals(taskInfoBo.getAppName())) {
                //启动
                if (IncidentEnum.START.getCode().equals(taskInfoBo.getIncident())) {
                    //检查任务是否在执行中
                    if (base.checkingTaskStatus(taskInfoBo.getId())) {
                        TaskInfo taskInfo = new TaskInfo();
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
                    base.stop(taskInfoBo.getId(), false);
                }
                //删除
                else if (IncidentEnum.REMOVE.getCode().equals(taskInfoBo.getIncident())) {
                    base.remove(taskInfoBo.getId(), true);
                }
            }
        }
    }
}
