package com.xiaomi.bms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaomi.bms.common.BaseResult;
import com.xiaomi.bms.entity.dto.VehicleWarnDto;

import com.xiaomi.bms.entity.vo.VehicleWarnVo;
import com.xiaomi.bms.service.VehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Api(tags = "车辆信息管理")
@RestController
@RequestMapping("/api")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @ApiOperation("电池信息预警")
    @PostMapping(value = "/warn")
    public BaseResult<List<VehicleWarnVo>> vehicleWarn(@RequestBody List<VehicleWarnDto> vehicleWarnVo) throws JsonProcessingException {
        return vehicleService.batterySignalWarn(vehicleWarnVo);
    }
//    @ApiOperation("电池预警")
//    @PostMapping(value = "/warn1")
//    public List<WarnRule> vehicleWarn1(@RequestBody List<VehicleWarnVo> vehicleWarnVo) {
//        return vehicleService.aa(vehicleWarnVo);
//    }
}
