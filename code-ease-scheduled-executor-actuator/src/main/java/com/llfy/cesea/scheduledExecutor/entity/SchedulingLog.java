package com.llfy.cesea.scheduledExecutor.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
@ApiModel(value = "SchedulingLog对象", description = "调度日志表")
public class SchedulingLog extends Model<SchedulingLog> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("执行器名称")
    private String actuator;

    @ApiModelProperty("携带参数")
    private String taskParam;

    @ApiModelProperty("完成状态")
    private Boolean completeState;

    @ApiModelProperty("响应结果")
    private String responseResult;

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

    public static final String TASK_ID = "task_id";

    public static final String ACTUATOR = "actuator";

    public static final String TASK_PARAM = "task_param";

    public static final String COMPLETE_STATE = "complete_state";

    public static final String RESPONSE_RESULT = "response_result";

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
