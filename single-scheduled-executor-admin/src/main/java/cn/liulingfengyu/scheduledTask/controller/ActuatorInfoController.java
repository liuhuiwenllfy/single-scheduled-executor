package cn.liulingfengyu.scheduledTask.controller;

import cn.liulingfengyu.actuator.dto.ActuatorInfoDto;
import cn.liulingfengyu.actuator.service.IActuatorInfoService;
import cn.liulingfengyu.actuator.vo.ActuatorInfoVo;
import cn.liulingfengyu.tools.exception.RespJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@AllArgsConstructor
public class ActuatorInfoController {

    private final IActuatorInfoService actuatorInfoService;

    /**
     * 条件查询执行器
     *
     * @param actuatorInfoDto 查询条件
     * @return {@link RespJson}
     */
    @GetMapping("getList")
    @Operation(summary = "条件查询执行器")
    public RespJson<List<ActuatorInfoVo>> getList(@Validated @ParameterObject ActuatorInfoDto actuatorInfoDto) {
        return RespJson.success(actuatorInfoService.getList(actuatorInfoDto));
    }

    /**
     * 批量删除执行器
     *
     * @param actuatorNames 执行器名称
     * @return {@link RespJson}
     */
    @DeleteMapping("deleteBatchByIdList")
    @Operation(summary = "批量删除执行器")
    public RespJson<Boolean> deleteBatchByIdList(
            @Parameter(description = "执行器逗号字符串", required = true)
            @RequestParam List<String> actuatorNames) {
        return RespJson.state(actuatorInfoService.deleteBatchByIdList(actuatorNames));
    }

}
