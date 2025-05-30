package cn.liulingfengyu.actuator.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallbackBo {

    /**
     * 全局唯一id
     */
    private String uuId;

    /**
     * 回调类型
     */
    private String incident;

    /**
     * 任务信息
     */
    private TaskInfoBo taskInfoBo;

    /**
     * 错误信息
     */
    private String errorMsg;
}
