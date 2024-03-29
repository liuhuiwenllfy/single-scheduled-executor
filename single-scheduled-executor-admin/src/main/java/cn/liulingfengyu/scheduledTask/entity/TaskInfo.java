package cn.liulingfengyu.scheduledTask.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 任务基表
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("s_task_info")
public class TaskInfo extends Model<TaskInfo> {

    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String APP_NAME = "app_name";
    public static final String TASK_PARAM = "task_param";
    public static final String CANCELLED = "cancelled";
    public static final String DONE = "done";
    public static final String NEXT_EXECUTION_TIME = "next_execution_time";
    public static final String CREATE_TIME = "create_time";
    public static final String TENANT_ID = "tenant_id";
    public static final String CRON = "cron";
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 标题
     */
    private String title;
    /**
     * 代码
     */
    private String code;
    /**
     * 执行器名称
     */
    private String appName;
    /**
     * 任务携带参数
     */
    private String taskParam;
    /**
     * 是否已取消
     */
    private boolean cancelled;
    /**
     * 是否已完成
     */
    private boolean done;
    /**
     * 下一次执行时间
     */
    private long nextExecutionTime;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 租户号
     */
    private String tenantId;

    /**
     * cron
     */
    private String cron;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
