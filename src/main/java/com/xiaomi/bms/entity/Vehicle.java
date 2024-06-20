package com.xiaomi.bms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="vehicle")
@ApiModel(description = "车辆信息")
public class Vehicle implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = "主键",notes = "16位雪花算法随机生成")
    private String id;

    /**
     * 创建时间
     */
    @TableField("created_time")
    @ApiModelProperty(value = "创建时间", example = "2024-06-15 00:00:00")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField("created_time")
    @ApiModelProperty(value = "更新时间", example = "2024-06-15 00:00:00")
    private Date updatedTime;

    /**
     * 车架编号
     */
    @TableField("vehicle_number")
    @ApiModelProperty(value = "车架编号", example = "1")
    private String vehicleNumber;

    /**
     * 电池类型外键ID
     */
    @TableField("battery_id")
    @ApiModelProperty(value = "电池类别")
    private String batteryId;

    /**
     * 总里程（km）
     */
    @TableField("total_mileage_km")
    @ApiModelProperty(value = "总里程（km）")
    private BigDecimal totalMileageKm;

    /**
     * 电池健康状态（%）
     */
    @TableField("battery_health_percentage")
    @ApiModelProperty(value = "电池健康状态（%）")
    private BigDecimal batteryHealthPercentage;

    /**
     * 逻辑删除
     */
    @TableField("is_deleted")
    @ApiModelProperty(value = "逻辑删除", example = "0",notes = "0未删除,1已删除")
    private Byte isDeleted;

    private static final long serialVersionUID = 1L;
}