package com.llfy.cesea.scheduledExecutor.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "TaskInfo对象", description = "任务基表")
public class TaskInfo extends Model<TaskInfo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("cron")
    private String cron;

    @ApiModelProperty("任务携带参数")
    private String taskParam;

    @ApiModelProperty("状态;1-未启用；2-启用；3-取消；4-未完成；5-完成")
    private Integer status;

    @ApiModelProperty("下一次执行时间")
    private String nextExecutionTime;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    @ApiModelProperty("删除时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date deleteTime;

    @ApiModelProperty("删除人")
    @TableField(fill = FieldFill.UPDATE)
    private String deleteUser;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Boolean isDelete;

    @ApiModelProperty("乐观锁")
    @Version
    private Integer version;

    @ApiModelProperty("租户号")
    private String tenantId;

    public static final String ID = "id";

    public static final String TITLE = "title";

    public static final String CRON = "cron";

    public static final String TASK_PARAM = "task_param";

    public static final String STATUS = "status";

    public static final String NEXT_EXECUTION_TIME = "next_execution_time";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

    public static final String DELETE_TIME = "delete_time";

    public static final String DELETE_USER = "delete_user";

    public static final String IS_DELETE = "is_delete";

    public static final String VERSION = "version";

    public static final String TENANT_ID = "tenant_id";

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
