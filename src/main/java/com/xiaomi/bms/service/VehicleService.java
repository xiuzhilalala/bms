package com.xiaomi.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaomi.bms.common.BaseResult;
import com.xiaomi.bms.entity.Vehicle;
import com.xiaomi.bms.entity.dto.VehicleWarnDto;

import com.xiaomi.bms.entity.vo.VehicleWarnVo;

import java.util.List;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
public interface VehicleService  extends IService<Vehicle> {

    BaseResult<List<VehicleWarnVo>>  batterySignalWarn(List<VehicleWarnDto> vehicleWarnVoList) throws JsonProcessingException;

}
