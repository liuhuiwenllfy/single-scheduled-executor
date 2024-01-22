package cn.liulingfengyu.mybatisplus.entity;


import lombok.Data;

/**
 * 分页包装类
 *
 * @author LLFY
 */
@Data
public class PageInfo {

    /**
     * 页码
     */
    private Integer page;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 正序排序字段逗号拼接字符串（对应数据库字段）
     */
    private String ascs;

    /**
     * 倒序排序字段逗号拼接字符串（对应数据库字段）
     */
    private String descs;
}
