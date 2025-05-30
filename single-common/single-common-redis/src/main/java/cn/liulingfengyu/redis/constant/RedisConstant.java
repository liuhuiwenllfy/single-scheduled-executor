package cn.liulingfengyu.redis.constant;

/**
 * redis分组名称常量
 */
public class RedisConstant {

    /**
     * 任务心跳
     */
    public static final String TASK_HEARTBEAT = "task_heartbeat:";

    /**
     * 任务注册表
     */
    public static final String TASK_REGISTRY = "task_registry";

    /**
     * 回调幂等id
     */
    public static final String CALLBACK_IDEMPOTENT = "callback_idempotent:";
}
