package cn.liulingfengyu.scheduledTask.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInfoPageDto {

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
}
