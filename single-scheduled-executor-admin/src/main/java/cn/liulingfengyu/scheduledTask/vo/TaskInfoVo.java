package cn.liulingfengyu.scheduledTask.vo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInfoVo {

    /**
     * 主键
     */
    private String id;
    /**
     * 代码
     */
    private String code;
    /**
     * 标题
     */
    private String title;
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
    private String nextExecutionTime;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     * cron
     */
    private String cron;
}
