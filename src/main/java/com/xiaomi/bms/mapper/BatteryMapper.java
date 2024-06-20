package com.xiaomi.bms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.bms.entity.Battery;
import org.springframework.stereotype.Repository;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */


@Repository
public interface BatteryMapper extends BaseMapper<Battery> {
    int deleteByPrimaryKey(String id);



    int insertSelective(Battery record);

    Battery selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Battery record);

    int updateByPrimaryKey(Battery record);



}