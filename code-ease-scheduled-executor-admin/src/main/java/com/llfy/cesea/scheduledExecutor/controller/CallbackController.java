package com.llfy.cesea.scheduledExecutor.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.llfy.cesea.scheduledExecutor.entity.TaskInfo;
import com.llfy.cesea.scheduledExecutor.enums.IncidentEnum;
import com.llfy.cesea.utils.RespJson;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 回调接口
     *
     * @param jsonObject 信息
     * @return {@link RespJson}
     */
    @PostMapping("callback")
    public String getThirdPartyPostInfo(@RequestBody JSONObject jsonObject) {
        String incident = jsonObject.getString("incident");
        RespJson respJson = JSON.parseObject(JSON.toJSONString(jsonObject.get("data")), RespJson.class);
        if (IncidentEnum.START.getCode().equals(incident)) {
            TaskInfo taskInfo = JSON.parseObject(JSON.toJSONString(respJson.getData()), TaskInfo.class);
            log.info("任务->{}在执行器->{}创建成功", taskInfo.getTitle(), taskInfo.getAppName());
        }
        if (IncidentEnum.CARRY_OUT.getCode().equals(incident)) {
            TaskInfo taskInfo = JSON.parseObject(JSON.toJSONString(respJson.getData()), TaskInfo.class);
            log.info("任务->{}在执行器->{}执行成功", taskInfo.getTitle(), taskInfo.getAppName());
        }
        if (IncidentEnum.STOP.getCode().equals(incident)) {
            TaskInfo taskInfo = JSON.parseObject(JSON.toJSONString(respJson.getData()), TaskInfo.class);
            log.info("任务->{}停止成功", taskInfo.getTitle());
        }
        if (IncidentEnum.REMOVE.getCode().equals(incident)) {
            TaskInfo taskInfo = JSON.parseObject(JSON.toJSONString(respJson.getData()), TaskInfo.class);
            log.info("任务->{}删除成功", taskInfo.getTitle());
        }
        if (IncidentEnum.UPDATE_STATUS.getCode().equals(incident)) {
            TaskInfo taskInfo = JSON.parseObject(JSON.toJSONString(respJson.getData()), TaskInfo.class);
            log.info("任务->{}执行状态更新成功", taskInfo.getTitle());
        }
        if (IncidentEnum.ERROR.getCode().equals(incident)) {
            String error = JSON.parseObject(JSON.toJSONString(respJson.getMsg()), String.class);
            log.info(error);
        }
        return "success";
    }
}
