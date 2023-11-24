package com.llfy.cesea.core.redis;

import com.alibaba.fastjson.JSON;
import com.llfy.cesea.core.redis.enums.IncidentEnum;
import com.llfy.cesea.scheduledExecutor.dto.MessageDto;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.service.BaseScheduledExecutorService;
import com.llfy.cesea.utils.RedisUtil;
import com.llfy.cesea.utils.RespJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订阅监听配置
 *
 * @author 刘凌枫羽工作室
 */
@Slf4j
@Component
public class ScheduledExecutorRedisMessageSubListener implements MessageListener {

    private final BaseScheduledExecutorService base;

    private final String appName;

    private final RedisUtil redisUtil;

    public ScheduledExecutorRedisMessageSubListener(
            BaseScheduledExecutorService base,
            @Value("${app.name}") String appName,
            RedisUtil redisUtil) {
        this.base = base;
        this.appName = appName;
        this.redisUtil = redisUtil;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("scheduledExecutor消费消息->{}", message.toString());
        MessageDto messageDto = JSON.parseObject(message.toString(), MessageDto.class);
        //启动
        if (IncidentEnum.START.getCode().equals(messageDto.getIncident())) {
            if (appName.equals(messageDto.getAppName())) {
                List<TaskInfo> taskDtoList = messageDto.getTaskInfoList();
                for (TaskInfo taskInfo : taskDtoList) {
                    //检查任务是否在执行中
                    Set<String> appNames = redisUtil.hKeys(base.actuatorName).stream().map(Object::toString).collect(Collectors.toSet());
                    boolean flag = false;
                    for (String item : appNames) {
                        if (redisUtil.hExists(item, taskInfo.getId())) {
                            TaskInfo ordTaskInfo = JSON.parseObject(redisUtil.hGet(item, taskInfo.getId()).toString(), TaskInfo.class);
                            if (!ordTaskInfo.isPeriodic() && !ordTaskInfo.isCancelled() && !ordTaskInfo.isDone()) {
                                flag = true;
                                break;
                            }
                            if (ordTaskInfo.isPeriodic() && !ordTaskInfo.isCancelled()) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        base.sendMessage(IncidentEnum.ERROR.getCode(), RespJson.error(taskInfo.getTitle().concat("->").concat("正在执行中")));
                        continue;
                    }
                    if (!taskInfo.isPeriodic()) {
                        base.startOnce(taskInfo, null);
                    } else {
                        base.startLoop(taskInfo, null);
                    }
                }
            }
        }
        //停止
        else if (IncidentEnum.STOP.getCode().equals(messageDto.getIncident())) {
            base.stop(messageDto.getId(), false);
        }
        //删除
        else if (IncidentEnum.REMOVE.getCode().equals(messageDto.getIncident())) {
            base.remove(messageDto.getId(), true);
        }
        //更新状态
        else if (IncidentEnum.UPDATE_STATUS.getCode().equals(messageDto.getIncident())) {
            base.updateStatus(messageDto.getId());
        }
        //任务转移
        else if (IncidentEnum.TRANSFER.getCode().equals(messageDto.getIncident())) {
            //校验消息目标是否是本机器
            if (appName.equals(messageDto.getAppName())) {
                //循环消息
                for (TaskInfo taskInfo : messageDto.getTaskInfoList()) {
                    base.restart(taskInfo);
                }
            }
        }
    }
}
