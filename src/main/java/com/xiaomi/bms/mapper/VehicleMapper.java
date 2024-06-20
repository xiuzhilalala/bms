package com.xiaomi.bms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.bms.entity.Vehicle;
import com.xiaomi.bms.entity.dto.VehicleWarnDto;
import com.xiaomi.bms.entity.dto.VehicleWarnRuleDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VehicleMapper extends BaseMapper<Vehicle> {
    int deleteByPrimaryKey(String id);

    int insertSelective(Vehicle record);

    Vehicle selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Vehicle record);

    int updateByPrimaryKey(Vehicle record);

    List<VehicleWarnRuleDto> selectAllByVehicleNumber(@Param("vehicleWarnDtoList") List<VehicleWarnDto> vehicleWarnDtoList);
    List<VehicleWarnRuleDto> selectAllByVehicleNumberTest(@Param("vehicleWarnDtoList") List<VehicleWarnDto> vehicleWarnDtoList);
}