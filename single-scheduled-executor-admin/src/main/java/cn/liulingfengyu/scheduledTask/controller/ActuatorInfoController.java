package cn.liulingfengyu.scheduledTask.controller;

import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.scheduledTask.dto.ActuatorInfoDto;
import cn.liulingfengyu.scheduledTask.service.IActuatorInfoService;
import cn.liulingfengyu.scheduledTask.vo.ActuatorInfoVo;
import cn.liulingfengyu.tools.exception.RespJson;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "执行器管理")
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
    @Operation(summary = "条件分页查询执行器")
    public RespJson<IPage<ActuatorInfoVo>> getByPage(@Validated @ParameterObject PageInfo pageInfo, @ParameterObject ActuatorInfoDto actuatorInfoDto) {
        return RespJson.success(actuatorInfoService.getByPage(pageInfo, actuatorInfoDto));
    }

    /**
     * 批量删除执行器
     *
     * @param idList 执行器ids
     * @return {@link RespJson}
     */
    @DeleteMapping("deleteBatchByIdList")
    @Operation(summary = "批量删除执行器")
    public RespJson<Boolean> deleteBatchByIdList(
            @Parameter(description = "执行器逗号字符串", required = true)
            @RequestParam List<String> idList) {
        return RespJson.state(actuatorInfoService.deleteBatchByIdList(idList));
    }

}
