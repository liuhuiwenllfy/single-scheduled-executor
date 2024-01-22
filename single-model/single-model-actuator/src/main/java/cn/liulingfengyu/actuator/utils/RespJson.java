package cn.liulingfengyu.actuator.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 返回json格式包装
 * {@code @JsonInclude} 过滤掉对象中为null的参数
 * {@code @NoArgsConstructor} 构建不参构造函数
 * {@code @JsonIgnore} 使之不在json返回结果当中
 *
 * @author LLFY
 */
@Data
@NoArgsConstructor
public class RespJson<T> {

    private int code;
    private String msg;
    private T data;
    private boolean ok;

    public RespJson(int code, String msg, boolean ok) {
        this.code = code;
        this.msg = msg;
        this.ok = ok;
    }

    public RespJson(int code, T data, boolean ok) {
        this.code = code;
        this.data = data;
        this.ok = ok;
    }

    public RespJson(int code, String msg, T data, boolean ok) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public static <T> RespJson<T> state(boolean isOk) {
        return isOk ? success() : error();
    }

    public static <T> RespJson<T> success() {
        return new RespJson<>(HttpStatus.OK.value(), "操作成功", true);
    }

    public static <T> RespJson<T> success(T data) {
        return new RespJson<>(HttpStatus.OK.value(), data, true);
    }

    public static <T> RespJson<T> success(String msg, T data) {
        return new RespJson<>(HttpStatus.OK.value(), msg, data, true);
    }

    public static <T> RespJson<T> error() {
        return new RespJson<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "操作失败", false);
    }

    public static <T> RespJson<T> error(String msg) {
        return new RespJson<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, false);
    }

    public static <T> RespJson<T> error(int code, String msg) {
        return new RespJson<>(code, msg, false);
    }

    public static <T> RespJson<T> error(int code, T data) {
        return new RespJson<>(code, data, false);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == HttpStatus.OK.value();
    }

}