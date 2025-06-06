package cn.liulingfengyu.scheduledTask.handler;

import cn.liulingfengyu.actuator.bo.CallbackBo;

public interface CallbackHandler {
    boolean supports(String taskCode);

    void handle(CallbackBo callbackBo);
}
