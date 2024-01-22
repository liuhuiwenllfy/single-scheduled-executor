package cn.liulingfengyu.scheduledTask.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.mybatisplus.utils.PageUtils;
import cn.liulingfengyu.scheduledTask.dto.TaskInfoPageDto;
import cn.liulingfengyu.scheduledTask.entity.TaskInfo;
import cn.liulingfengyu.scheduledTask.mapper.TaskInfoMapper;
import cn.liulingfengyu.scheduledTask.service.ITaskInfoService;
import cn.liulingfengyu.scheduledTask.vo.TaskInfoVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务基表 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
@DS("scheduled_task")
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements ITaskInfoService {

    @Override
    public IPage<TaskInfoVo> getByPage(PageInfo pageInfo, TaskInfoPageDto taskInfoPageDto) {
        Page<TaskInfoVo> page = new PageUtils<TaskInfoVo>().getPage(pageInfo);
        List<TaskInfoVo> list = baseMapper.getByPage(page, taskInfoPageDto);
        list.forEach(item -> {
            if (StrUtil.isNotBlank(item.getNextExecutionTime())) {
                // 将时间戳转换为Instant对象
                Instant instant = Instant.ofEpochMilli(Long.parseLong(item.getNextExecutionTime()));
                // 将Instant对象转换为Date对象
                Date date = Date.from(instant);
                // 设置日期格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                item.setNextExecutionTime(sdf.format(date));
            }

        });
        page.setRecords(list);
        return page;
    }

    @Override
    public TaskInfoVo queryById(String id) {
        return baseMapper.queryById(id);
    }
}
