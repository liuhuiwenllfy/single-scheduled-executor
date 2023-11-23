package com.llfy.cesea.core.redis.enums;

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
     * CARRY_OUT->执行
     * STOP->停止
     * REMOVE->删除
     * UPDATE_STATUS->更新状态
     * TRANSFER->任务转移
     * ERROR->错误消息
     */
    START("START", "启动"),
    CARRY_OUT("CARRY_OUT", "执行"),
    STOP("STOP", "停止"),
    REMOVE("REMOVE", "删除"),
    UPDATE_STATUS("UPDATE_STATUS", "更新状态"),
    TRANSFER("TRANSFER", "任务转移"),
    ERROR("ERROR", "错误消息");


    private final String code;

    private final String msg;
}
