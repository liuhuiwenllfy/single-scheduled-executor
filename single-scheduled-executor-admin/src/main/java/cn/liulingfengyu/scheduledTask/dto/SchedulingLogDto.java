package cn.liulingfengyu.scheduledTask.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingLogDto {

    /**
     * 执行器名称
     */
    private String appName;
    /**
     * 完成状态
     */
    private Boolean done;
    /**
     * 开始时间
     */
    private String startCreateTime;
    /**
     * 结束时间
     */
    private String endCreateTime;
}
