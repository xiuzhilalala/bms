package com.xiaomi.bms.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;

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
@TableName(value="battery")
@ApiModel(description = "电池信息")
public class Battery implements Serializable {
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
     * 电池类型
     */
    @ApiModelProperty(value = "电池类型", example = "三元电池")
    @TableField("battery_type")
    private String batteryType;

    /**
     * 逻辑删除
     */
    @TableField("is_deleted")
    @ApiModelProperty(value = "逻辑删除", example = "0",notes = "0未删除,1已删除")
    private Byte isDeleted;

    private static final long serialVersionUID = 1L;
}