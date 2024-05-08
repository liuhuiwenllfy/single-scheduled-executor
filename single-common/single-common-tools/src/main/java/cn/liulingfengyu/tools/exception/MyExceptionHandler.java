package cn.liulingfengyu.tools.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义异常处理
 *
 * @author LLFY
 */
@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    /**
     * 自定义验证异常
     *
     * @param e 异常对象
     * @return {@link Object}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> list = new ArrayList<>();
        try {
            fieldErrors.forEach(item ->
                    list.add(item.getDefaultMessage()));
        } catch (Exception exception) {
            fieldErrors.forEach(item -> list.add(item.getDefaultMessage()));
        }
        return RespJson.error(400, list);
    }

    /**
     * 自定义验证异常
     *
     * @param e 异常对象
     * @return {@link Object}
     */
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindingResult e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<String> list = new ArrayList<>();
        try {
            fieldErrors.forEach(item ->
                    list.add(item.getDefaultMessage()));
        } catch (Exception exception) {
            fieldErrors.forEach(item -> list.add(item.getDefaultMessage()));
        }
        return RespJson.error(400, list);
    }

    /**
     * 自定义通用异常
     *
     * @param e 异常对象
     * @return {@link Object}
     */
    @ExceptionHandler(MyException.class)
    public Object myExceptionHandler(MyException e) {
        log.error(e.getMessage(), e);
        return RespJson.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 自定义通用异常
     *
     * @param e 异常对象
     * @return {@link Object}
     */
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return RespJson.error(ErrorCode.ERROR.getCode(), ErrorCode.ERROR.getMessage());
    }
}
