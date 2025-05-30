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
     * 任务信息
     */
    private TaskInfoBo taskInfoBo;

    /**
     * 错误信息
     */
    private String errorMsg;
}
