package cn.liulingfengyu.scheduledTask.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingLogVo {

    /**
     * 主键
     */
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
    private String createTime;
}
