package com.xiaomi.bms.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "汽车电池预警Vo")
public class VehicleWarnVo{
    private Integer vehicleNumber;
    private String batteryType;
    private String warnName;
    private Integer warnLevel;
}
