package cn.liulingfengyu.mybatisplus.utils;

import cn.hutool.core.util.StrUtil;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * pageInfo转换为page
 *
 * @param <T> page
 * @author LLFY
 */
public class PageUtils<T> {

    /**
     * pageInfo转换为page
     *
     * @param pageInfo 分页对象
     * @return {@link Page}
     */
    public Page<T> getPage(PageInfo pageInfo) {
        Page<T> page = new Page<>();
        page.setCurrent(pageInfo.getPage());
        page.setSize(pageInfo.getPageSize());
        List<OrderItem> orderItemList = new ArrayList<>();
        if (StringUtils.isNotBlank(pageInfo.getAscs())) {
            String[] split = pageInfo.getAscs().split(",");
            List<String> list = new ArrayList<>();
            for (String asc : split) {
                list.add(StrUtil.toUnderlineCase(asc));
            }
            orderItemList.addAll(OrderItem.ascs(list.toArray(new String[0])));
        }
        if (StringUtils.isNotBlank(pageInfo.getDescs())) {
            String[] split = pageInfo.getDescs().split(",");
            List<String> list = new ArrayList<>();
            for (String desc : split) {
                list.add(StrUtil.toUnderlineCase(desc));
            }
            orderItemList.addAll(OrderItem.descs(list.toArray(new String[0])));
        }
        page.setOrders(orderItemList);
        return page;
    }
}
