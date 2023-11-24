package com.llfy.cesea.scheduledExecutor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llfy.cesea.scheduledExecutor.dto.ActuatorInfoDto;
import com.llfy.cesea.scheduledExecutor.entity.ActuatorInfo;
import com.llfy.cesea.scheduledExecutor.service.IActuatorInfoService;
import com.llfy.cesea.utils.RespJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param page 分页参数
     * @return {@link RespJson}
     */
    @GetMapping("getByPage")
    public RespJson<IPage<ActuatorInfo>> getByPage(Page<ActuatorInfo> page, ActuatorInfoDto actuatorInfoDto) {
        return RespJson.success(actuatorInfoService.getByPage(page, actuatorInfoDto));
    }

}
