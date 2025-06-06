package cn.liulingfengyu.actuator.service;

import cn.liulingfengyu.actuator.dto.TaskInfoPageDto;
import cn.liulingfengyu.actuator.dto.TaskInsertDto;
import cn.liulingfengyu.actuator.dto.TaskUpdateDto;
import cn.liulingfengyu.actuator.entity.TaskInfo;
import cn.liulingfengyu.actuator.vo.TaskInfoVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 获取指定执行器需要重启的任务
     *
     * @param actuatorName 执行器名称
     * @return {@link List}
     */
    List<TaskInfo> getRestartList(String actuatorName);

    /**
     * 创建任务
     *
     * @param taskInsertDto 入参
     */
    void insertItem(TaskInsertDto taskInsertDto);

    /**
     * 启动任务
     *
     * @param id 入参
     */
    void start(String id);

    /**
     * 启动任务
     *
     * @param taskInfo 入参
     */
    void start(TaskInfo taskInfo);

    /**
     * 修改任务
     *
     * @param taskUpdateDto 入参
     */
    void updateItem(TaskUpdateDto taskUpdateDto);

    /**
     * 暂停任务
     *
     * @param taskId 任务id
     */
    void stop(String taskId);

    /**
     * 删除任务
     *
     * @param taskId 任务id
     */
    void remove(String taskId);
}
