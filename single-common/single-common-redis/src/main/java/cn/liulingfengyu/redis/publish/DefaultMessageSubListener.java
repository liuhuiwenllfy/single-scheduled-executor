package cn.liulingfengyu.redis.publish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 订阅监听配置
 *
 * @author 30346
 */
@Slf4j
@Component
public class DefaultMessageSubListener extends MessageSubListener {

    @Override
    public void onMessage() {
        log.info("执行了默认监听");
    }
}
