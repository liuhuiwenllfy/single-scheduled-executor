package cn.liulingfengyu.scheduledTask.handler;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.scheduledTask.enums.InterfaceEnum;
import cn.liulingfengyu.scheduledTask.service.ISchedulingLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteActuatorLogCallbackHandler implements CallbackHandler {

    @Autowired
    private ISchedulingLogService schedulingLogService;

    @Override
    public boolean supports(String taskCode) {
        return InterfaceEnum.DELETE_THIRTY_DAYS_AGO_ACTUATOR_LOGS.getCode().equals(taskCode);
    }

    @Override
    public void handle(CallbackBo callbackBo) {
        schedulingLogService.deleteThirtyDaysAgoActuatorLogs();
        log.info("任务->{}，执行器->{}，消息->{}",
                callbackBo.getTaskInfoBo().getTitle(),
                callbackBo.getTaskInfoBo().getAppName(),
                callbackBo.getErrorMsg());
    }
}
