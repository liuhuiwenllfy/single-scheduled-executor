package com.llfy.cesea.scheduledExecutor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 调度日志表
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("s_scheduling_log")
public class SchedulingLog extends Model<SchedulingLog> {

    public static final String ID = "id";
    public static final String TASK_ID = "task_id";
    public static final String APP_NAME = "app_name";
    public static final String TASK_PARAM = "task_param";
    public static final String COMPLETE_STATE = "complete_state";
    public static final String RESPONSE_RESULT = "response_result";
    public static final String CREATE_TIME = "create_time";
    public static final String TENANT_ID = "tenant_id";
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 执行器名称
     */
    private String appName;
    /**
     * 携带参数
     */
    private String taskParam;
    /**
     * 完成状态
     */
    private boolean done;
    /**
     * 响应结果
     */
    private String responseResult;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 租户号
     */
    private String tenantId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
