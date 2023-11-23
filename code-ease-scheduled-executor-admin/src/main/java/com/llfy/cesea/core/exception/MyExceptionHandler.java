package com.llfy.cesea.core.exception;

import com.llfy.cesea.utils.RespJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义异常处理
 *
 * @author 刘凌枫羽工作室
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
        return getListRespJson(fieldErrors);
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
        return getListRespJson(fieldErrors);
    }

    private static RespJson<List<String>> getListRespJson(List<FieldError> fieldErrors) {
        List<String> list = new ArrayList<>();
        try {
            fieldErrors.forEach(item ->
                    list.add(Objects.requireNonNull(item.getDefaultMessage())));
        } catch (Exception exception) {
            fieldErrors.forEach(item -> list.add(item.getDefaultMessage()));
        }
        return RespJson.error(HttpStatus.BAD_REQUEST.value(), list);
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
        return RespJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}
