package cn.liulingfengyu.scheduledTask.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDto {

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
     * cron
     */
    private String cron;

    /**
     * 任务携带参数
     */
    private String taskParam;
}
