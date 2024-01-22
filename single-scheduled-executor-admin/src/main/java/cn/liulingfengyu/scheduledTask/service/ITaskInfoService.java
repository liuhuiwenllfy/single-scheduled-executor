package cn.liulingfengyu.scheduledTask.service;

import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.TaskInfoPageDto;
import cn.liulingfengyu.scheduledTask.entity.TaskInfo;
import cn.liulingfengyu.scheduledTask.vo.TaskInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 任务基表 服务类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
public interface ITaskInfoService extends IService<TaskInfo> {

    /**
     * 条件分页查询任务
     *
     * @param taskInfoPageDto 分页参数
     * @return {@link IPage}
     */
    IPage<TaskInfoVo> getByPage(PageInfo pageInfo, TaskInfoPageDto taskInfoPageDto);

    /**
     * 根据任务id查询任务
     *
     * @param id 任务id
     * @return {@link TaskInfoVo}
     */
    TaskInfoVo queryById(String id);
}
