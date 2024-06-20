package com.xiaomi.bms.entity.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuzhe1018
 * @date 2024/6/19 16:41
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "汽车电池预警dto（包含batteryId和batteryType）")
public class VehicleWarnRuleDto extends VehicleWarnDto {
    private String batteryId;
    private String batteryType;
}
