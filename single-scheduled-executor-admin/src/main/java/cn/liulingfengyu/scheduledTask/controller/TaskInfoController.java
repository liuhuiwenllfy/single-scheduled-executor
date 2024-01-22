package cn.liulingfengyu.scheduledTask.controller;

import cn.liulingfengyu.actuator.utils.CronUtils;
import cn.liulingfengyu.actuator.utils.RespJson;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.TaskInfoPageDto;
import cn.liulingfengyu.scheduledTask.dto.TaskInsertDto;
import cn.liulingfengyu.scheduledTask.dto.TaskUpdateDto;
import cn.liulingfengyu.scheduledTask.service.IScheduledExecutorService;
import cn.liulingfengyu.scheduledTask.service.ITaskInfoService;
import cn.liulingfengyu.scheduledTask.vo.TaskInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 任务基表 前端控制器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/scheduledExecutor/taskInfo")
public class TaskInfoController {

    @Autowired
    private IScheduledExecutorService scheduledExecutorService;

    @Autowired
    private ITaskInfoService taskInfoService;

    /**
     * 条件分页查询任务
     *
     * @param taskInfoPageDto 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    public RespJson<IPage<TaskInfoVo>> getByPage(PageInfo pageInfo, TaskInfoPageDto taskInfoPageDto) {
        return RespJson.success(taskInfoService.getByPage(pageInfo, taskInfoPageDto));
    }

    /**
     * 根据任务id查询任务
     *
     * @param id 任务id
     * @return {@link RespJson}
     */
    @GetMapping("queryById")
    public RespJson<TaskInfoVo> queryById(String id) {
        return RespJson.success(taskInfoService.queryById(id));
    }

    /**
     * 创建任务
     *
     * @param taskInsertDto 入参
     */
    @PostMapping("insert")
    public RespJson<Boolean> insertItem(@RequestBody TaskInsertDto taskInsertDto) {
        if (CronUtils.getNextTimeDelayMilliseconds(taskInsertDto.getCron()) == -1) {
            return RespJson.error("cron格式错误");
        }
        scheduledExecutorService.insertItem(taskInsertDto);
        return RespJson.state(true);
    }

    /**
     * 启动任务
     *
     * @param id 任务id
     */
    @PostMapping("updateStart")
    public RespJson<Boolean> start(@RequestParam String id) {
        scheduledExecutorService.start(id);
        return RespJson.state(true);
    }

    /**
     * 修改任务
     *
     * @param taskUpdateDto 入参
     */
    @PutMapping("updateById")
    public RespJson<Boolean> updateItem(@RequestBody TaskUpdateDto taskUpdateDto) {
        if (CronUtils.getNextTimeDelayMilliseconds(taskUpdateDto.getCron()) == -1) {
            return RespJson.error("cron格式错误");
        }
        scheduledExecutorService.updateItem(taskUpdateDto);
        return RespJson.state(true);
    }

    /**
     * 暂停任务
     *
     * @param id 任务id
     */
    @PutMapping("updateStop")
    public RespJson<Boolean> stop(@RequestParam String id) {
        scheduledExecutorService.stop(id);
        return RespJson.state(true);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     */
    @DeleteMapping("deleteById")
    public RespJson<Boolean> remove(String id) {
        scheduledExecutorService.remove(id);
        return RespJson.state(true);
    }
}
