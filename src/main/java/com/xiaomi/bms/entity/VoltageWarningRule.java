//package com.xiaomi.bms.entity;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * @author zhuzhe1018
// * @date 2024/6/16
// */
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@TableName(value = "voltage_warn_rule")
//@ApiModel(description = "电压预警规则")
//public class VoltageWarningRule implements Serializable {
//    /**
//     * 主键ID
//     */
//    @TableId(type = IdType.NONE)
//    @ApiModelProperty(value = "主键", notes = "16位雪花算法随机生成")
//    private String id;
//
//    /**
//     * 规则编号ID（1为电压规则，2为电流规则）
//     */
//    private String ruleId;
//
//    /**
//     * 创建时间
//     */
//    @TableField("created_time")
//    @ApiModelProperty(value = "创建时间", example = "2024-06-15 00:00:00")
//    private Date createdTime;
//
//    /**
//     * 更新时间
//     */
//    @TableField("created_time")
//    @ApiModelProperty(value = "更新时间", example = "2024-06-15 00:00:00")
//    private Date updatedTime;
//
//    /**
//     * 电池类型ID
//     */
//    @ApiModelProperty(value = "电池ID")
//    @TableField("battery_id")
//    private String batteryId;
//
//    /**
//     * 电流下限
//     */
//    @ApiModelProperty(value = "阈值下限")
//    @TableField("lower_limit")
//    private BigDecimal lowerLimit;
//
//    /**
//     * 电流上限
//     */
//    @ApiModelProperty(value = "阈值上限")
//    @TableField("upper_limit")
//    private BigDecimal upperLimit;
//
//    /**
//     * 警报等级
//     */
//    @ApiModelProperty(value = "预警等级", example = "0", notes = "预警等级可以是以下值之一：-1(未预警), 0, 1, 2, 3,4")
//    @TableField("warn_level")
//    private Integer warnLevel;
//
//    /**
//     * 逻辑删除
//     */
//    @TableField("is_deleted")
//    @ApiModelProperty(value = "逻辑删除", example = "0", notes = "0未删除,1已删除")
//    private Byte isDeleted;
//
//    private static final long serialVersionUID = 1L;
//}