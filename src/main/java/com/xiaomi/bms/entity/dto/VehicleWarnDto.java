package com.xiaomi.bms.entity.dto;

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
@ApiModel(description = "汽车电池预警dto")
public class VehicleWarnDto {
    private Integer carId;
    private Integer warnId;
    private String signal;

}
