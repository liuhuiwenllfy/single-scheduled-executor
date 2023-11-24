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
 * 执行器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("s_actuator_info")
public class ActuatorInfo extends Model<ActuatorInfo> {

    public static final String ID = "id";
    public static final String ACTUATOR_NAME = "actuator_name";
    public static final String ACTUATOR_IP = "actuator_ip";
    public static final String CREATE_TIME = "create_time";
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 执行器名称
     */
    private String actuatorName;
    /**
     * 执行器ip
     */
    private String actuatorIp;
    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
