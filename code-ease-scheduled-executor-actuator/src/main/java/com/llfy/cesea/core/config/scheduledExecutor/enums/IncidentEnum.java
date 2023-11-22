package com.llfy.cesea.core.config.scheduledExecutor.enums;

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
     * STOP->停止
     * REMOVE->删除
     * UPDATE_STATUS->更新状态
     * PUSH_EXECUTION->推送执行中状态
     * TRANSFER->任务转移
     */
    START("START", "启动"),
    CARRY_OUT("CARRY_OUT", "执行"),
    STOP("STOP", "停止"),
    REMOVE("REMOVE", "删除"),
    UPDATE_STATUS("UPDATE_STATUS", "更新状态"),
    PUSH_EXECUTION("PUSH_EXECUTION", "推送执行中状态"),
    TRANSFER("TRANSFER", "任务转移");


    private final String code;

    private final String msg;
}
