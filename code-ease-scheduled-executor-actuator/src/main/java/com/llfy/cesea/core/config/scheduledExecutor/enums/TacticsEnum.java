package com.llfy.cesea.core.config.scheduledExecutor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 策略枚举
 *
 * @author 刘凌枫羽工作室
 */
@Getter
@AllArgsConstructor
public enum TacticsEnum {

    /**
     * 1->轮询
     * 2->随机
     */
    POLLING(1, "轮询"),
    RANDOM(2, "随机");

    private final Integer code;

    private final String msg;

}
