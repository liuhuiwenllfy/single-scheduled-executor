package cn.liulingfengyu.actuator.handler;

import cn.liulingfengyu.actuator.bo.CallbackBo;

public interface CallbackHandler {
    boolean supports(String taskCode);

    void handle(CallbackBo callbackBo);
}
