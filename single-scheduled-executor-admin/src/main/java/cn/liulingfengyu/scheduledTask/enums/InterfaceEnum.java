package cn.liulingfengyu.scheduledTask.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口枚举
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@AllArgsConstructor
public enum InterfaceEnum {

    DELETE_THIRTY_DAYS_AGO_ACTUATOR_LOGS("deleteThirtyDaysAgoActuatorLogs", "删除30天前的执行器日志"),
    ;

    private final String code;

    private final String msg;
}
