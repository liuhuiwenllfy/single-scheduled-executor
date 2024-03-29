package cn.liulingfengyu.actuator.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallbackBo {

    /**
     * 回调类型
     */
    String incident;

    /**
     * 任务信息
     */
    TaskInfoBo taskInfoBo;

    /**
     * 错误信息
     */
    String errorMsg;
}
