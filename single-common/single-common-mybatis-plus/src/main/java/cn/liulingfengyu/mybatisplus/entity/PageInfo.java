package cn.liulingfengyu.mybatisplus.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 分页包装类
 *
 * @author LLFY
 */
@Data
@Schema(name = "PageInfo")
public class PageInfo {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"1", "2", "3"})
    @NotNull(message = "页码不能为空")
    private Integer page;

    @Schema(description = "页大小", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"10", "20", "50"})
    @NotNull(message = "页大小不能为空")
    private Integer pageSize;

    @Schema(description = "正序排序字段逗号拼接字符串（对应数据库字段）")
    private String ascs;

    @Schema(description = "倒序排序字段逗号拼接字符串（对应数据库字段）")
    private String descs;
}
