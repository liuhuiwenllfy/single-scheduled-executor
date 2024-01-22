package cn.liulingfengyu.actuator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件枚举
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@AllArgsConstructor
public enum IncidentEnum {

    /**
     * START->启动
     * UPDATE->修改
     * CARRY_OUT->执行
     * STOP->停止
     * REMOVE->删除
     * TRANSFER->任务转移
     * ERROR->错误消息
     */
    START("START", "启动"),
    UPDATE("UPDATE", "修改"),
    CARRY_OUT("CARRY_OUT", "执行"),
    STOP("STOP", "停止"),
    REMOVE("REMOVE", "删除"),
    ERROR("ERROR", "错误消息");


    private final String code;

    private final String msg;
}
