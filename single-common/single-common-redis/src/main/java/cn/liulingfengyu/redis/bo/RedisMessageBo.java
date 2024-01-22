package cn.liulingfengyu.redis.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisMessageBo {

    /**
     * 全局唯一id
     */
    private String uuid;

    /**
     * 其他信息
     */
    private String message;
}
