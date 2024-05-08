package cn.liulingfengyu.tools.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常异常
 *
 * @author LLFY
 */
@SuppressWarnings("unused")
@Getter
@Setter
public class MyException extends RuntimeException {

    private int errorCode;

    public MyException(String message) {
        super(message);
    }

    public MyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }
}
