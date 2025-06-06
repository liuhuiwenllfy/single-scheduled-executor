package cn.liulingfengyu.tools.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常码类
 *
 * @author 30346
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    ERROR(500, "程序异常，请联系管理"),

    CRON_ERROR(60001, "cron格式错误"),

    TASK_NOT_EXIST(60002, "任务不存在"),
    ;

    private final int code;

    private final String message;
}
