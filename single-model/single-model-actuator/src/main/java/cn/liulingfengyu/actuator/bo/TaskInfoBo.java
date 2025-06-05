package cn.liulingfengyu.actuator.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInfoBo {

    /**
     * 任务id
     */
    private String id;

    /**
     * 执行器名称
     */
    private String appName;

    /**
     * 代码
     */
    private String code;

    /**
     * 标题
     */
    private String title;
    /**
     * 任务携带参数
     */
    private String taskParam;
    /**
     * 是否已取消
     */
    private boolean cancelled;

    /**
     * cron
     */
    private String cron;

    /**
     * 是否已完成
     */
    private boolean done;

    /**
     * 消息类型
     */
    private String incident;

    /**
     * 是否强制停止
     */
    private boolean forcedStop;
}
