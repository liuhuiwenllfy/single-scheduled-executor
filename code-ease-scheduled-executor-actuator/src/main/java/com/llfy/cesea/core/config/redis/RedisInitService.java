package com.llfy.cesea.core.config.redis;

import com.llfy.cesea.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * redis初始化服务
 *
 * @author 刘凌枫羽工作室
 */
@Component
public class RedisInitService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 初始化参数
     */
    public void initializeRedis() {

    }
}
