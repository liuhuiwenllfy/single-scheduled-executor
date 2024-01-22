package cn.liulingfengyu.scheduledTask.vo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorInfoVo {

    /**
     * 主键
     */
    private String id;

    /**
     * 执行器名称
     */
    private String actuatorName;
    /**
     * 执行器ip
     */
    private String actuatorIp;

    /**
     * 是否正常
     */
    private Boolean isNormal;
}
