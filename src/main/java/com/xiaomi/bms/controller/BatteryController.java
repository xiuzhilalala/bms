package com.xiaomi.bms.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Api(tags = "电池信息管理")
@RestController
@RequestMapping("/battery")
public class BatteryController {

//    @ApiOperation("电池预警")
//    @PostMapping(value = "/warn")
//    public Result batteryWarn(@RequestBody String params) {
//        return equipmentService.getAllequipment(params);
//    }
}
