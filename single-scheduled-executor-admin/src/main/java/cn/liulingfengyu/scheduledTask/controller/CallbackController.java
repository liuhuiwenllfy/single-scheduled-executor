package cn.liulingfengyu.scheduledTask.controller;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.enums.IncidentEnum;
import cn.liulingfengyu.actuator.utils.RespJson;
import cn.liulingfengyu.scheduledTask.enums.InterfaceEnum;
import cn.liulingfengyu.scheduledTask.service.ISchedulingLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 任务基表 前端控制器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/scheduledExecutor")
@Slf4j
public class CallbackController {

    @Autowired
    private ISchedulingLogService schedulingLogService;

    /**
     * 回调接口
     *
     * @param callbackBo 信息
     * @return {@link RespJson}
     */
    @PostMapping("callback")
    public String callback(@RequestBody CallbackBo callbackBo) {
        String incident = callbackBo.getIncident();
        TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
        if (IncidentEnum.CARRY_OUT.getCode().equals(incident)) {
            //删除三十天前的执行器日志
            if (taskInfoBo.getCode().equals(InterfaceEnum.DELETE_THIRTY_DAYS_AGO_ACTUATOR_LOGS.getCode())) {
                schedulingLogService.deleteThirtyDaysAgoActuatorLogs();
            }
        }
        log.info("任务->{}，执行器->{}，消息->{}", taskInfoBo.getTitle(), taskInfoBo.getAppName(), callbackBo.getErrorMsg());
        return "success";
    }
}
