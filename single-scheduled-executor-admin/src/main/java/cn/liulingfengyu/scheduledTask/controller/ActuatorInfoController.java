package cn.liulingfengyu.scheduledTask.controller;

import cn.liulingfengyu.actuator.utils.RespJson;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.ActuatorInfoDto;
import cn.liulingfengyu.scheduledTask.service.IActuatorInfoService;
import cn.liulingfengyu.scheduledTask.vo.ActuatorInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 执行器 前端控制器
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@RestController
@RequestMapping("/scheduledExecutor/actuatorInfo")
public class ActuatorInfoController {

    @Autowired
    private IActuatorInfoService actuatorInfoService;

    /**
     * 条件分页查询执行器
     *
     * @param pageInfo 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    public RespJson<IPage<ActuatorInfoVo>> getByPage(PageInfo pageInfo, ActuatorInfoDto actuatorInfoDto) {
        return RespJson.success(actuatorInfoService.getByPage(pageInfo, actuatorInfoDto));
    }

    /**
     * 批量删除执行器
     *
     * @param idList 执行器ids
     * @return {@link RespJson}
     */
    @DeleteMapping("deleteBatchByIdList")
    public RespJson deleteBatchByIdList(@RequestParam List<String> idList) {
        return RespJson.state(actuatorInfoService.deleteBatchByIdList(idList));
    }

}
