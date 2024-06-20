package com.xiaomi.bms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.bms.entity.WarnRule;
import com.xiaomi.bms.entity.dto.VehicleWarnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Repository
@Mapper
public interface WarnRuleMapper extends BaseMapper<WarnRule> {
    int deleteByPrimaryKey(String id);

    int insertSelective(WarnRule record);

    WarnRule selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(WarnRule record);

    int updateByPrimaryKey(WarnRule record);



    //根据batteryId查询该电池预警规则
    List<WarnRule> selectAllWarnRuleByBatteryIds(@Param("batteryIdsToGetFromDb") List<String> batteryIdsToGetFromDb);
    //根据规则Id删除warnRule
    int deleteWarnRuleByIds(@Param("warnRuleList") List<WarnRule> warnRuleList);
    //根据传入的数据更新
    int updateBatchById(@Param("warnRule") WarnRule warnRule);
    //根据传入的数据查询规则
    List<WarnRule> selectWarnRule(@Param("warnRule") WarnRule warnRule);

}