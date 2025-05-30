package cn.liulingfengyu.redis.constant;

/**
 * redis分组名称常量
 */
public class RedisConstant {

    /**
     * 执行器心跳
     */
    public static final String ACTUATOR_HEARTBEAT = "actuator_heartbeat:";

    /**
     * 调度中心注册表
     */
    public static final String ACTUATOR_REGISTRY = "actuator_registry:";

    /**
     * 回调幂等id
     */
    public static final String CALLBACK_IDEMPOTENT = "callback_idempotent:";

    /**
     * 执行消息幂等id
     */
    public static final String MESSAGE_IDEMPOTENT = "message_idempotent:";
}
