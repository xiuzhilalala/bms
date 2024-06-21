package com.xiaomi.bms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaomi.bms.common.BaseResult;
import com.xiaomi.bms.constant.RedisConstant;
import com.xiaomi.bms.constant.ResultConstant;
import com.xiaomi.bms.entity.Battery;
import com.xiaomi.bms.entity.WarnRule;
import com.xiaomi.bms.entity.dto.VehicleWarnDto;
import com.xiaomi.bms.entity.Vehicle;
import com.xiaomi.bms.entity.dto.VehicleWarnRuleDto;
import com.xiaomi.bms.entity.vo.VehicleWarnVo;
import com.xiaomi.bms.mapper.BatteryMapper;
import com.xiaomi.bms.mapper.VehicleMapper;
import com.xiaomi.bms.mapper.WarnRuleMapper;
import com.xiaomi.bms.service.VehicleService;
import com.xiaomi.bms.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 该实现类主要是对关于车辆信息的相关操作，包括车辆电池信息的预警。
 * @author zhuzhe1018
 * @date 2024/6/17
 */
@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    @Autowired
    private WarnRuleMapper warnRuleMapper;
    @Autowired
    private BatteryMapper batteryMapper;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VehicleMapper vehicleMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 该方法是对前端穿入的vehicleWarnDtoList所有的车辆电池信息进行预警
     *
     * @param vehicleWarnDtoList 前端传入参数
     * @return BaseResult<List < VehicleWarnVo>>
     * @throws JsonProcessingException jackson解析Json失败
     * @Author zhuzhe1018 朱哲
     * @date 2024/6/17
     * @Version 1.0
     */
    @Override
    public BaseResult<List<VehicleWarnVo>> batterySignalWarn(List<VehicleWarnDto> vehicleWarnDtoList) throws JsonProcessingException {
        // 1. 整理所有需要预警的vehicleID
//        long startTime = System.currentTimeMillis();
//        List<VehicleWarnRuleDto> vehicleWarnRuleDtoList = vehicleMapper.selectAllByVehicleNumber(vehicleWarnDtoList);
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time taken by selectAllByVehicleNumber: " + (endTime - startTime) + " ms");
//
//        // 构建 carId 到 VehicleWarnDto 的映射
//        Map<Integer, VehicleWarnDto> warnDtoMap = vehicleWarnDtoList.stream()
//                .collect(Collectors.toMap(VehicleWarnDto::getCarId, Function.identity(), (oldKey, newKey) -> newKey));
//
//
//
//        for (VehicleWarnRuleDto vehicleWarnRuleDto : vehicleWarnRuleDtoList) {
//            VehicleWarnDto vehicleWarnDto = warnDtoMap.get(vehicleWarnRuleDto.getCarId());
//            if (vehicleWarnDto != null) {
//                vehicleWarnRuleDto.setSignal(vehicleWarnDto.getSignal());
//                vehicleWarnRuleDto.setWarnId(vehicleWarnDto.getWarnId());
//            }
//        }
//
        long startTime = System.currentTimeMillis();
        List<VehicleWarnRuleDto> vehicleWarnRuleDtoList = vehicleMapper.selectAllByVehicleNumberTest(vehicleWarnDtoList);
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken by selectAllByVehicleNumber: " + (endTime - startTime) + " ms");
        List<String> warnBatteryIds = vehicleWarnRuleDtoList.stream()
                .map(VehicleWarnRuleDto::getBatteryId)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.toList());

        QueryWrapper<Battery> batteryQueryWrapper = new QueryWrapper<>();
        batteryQueryWrapper.in("id", warnBatteryIds);
        List<Battery> batteryList = batteryMapper.selectList(batteryQueryWrapper);

        Map<String, Battery> batteryIdMap = batteryList.stream()
                .collect(Collectors.toMap(Battery::getId, Function.identity(), (oldKey, newKey) -> newKey));
        Map<Integer, VehicleWarnDto> vehicleWarnDtoCarIdMap = vehicleWarnDtoList.stream()
                .collect(Collectors.toMap(VehicleWarnDto::getCarId, Function.identity(), (oldKey, newKey) -> newKey));


        for (VehicleWarnRuleDto vehicleWarnRuleDto : vehicleWarnRuleDtoList) {
            Battery battery = batteryIdMap.get(vehicleWarnRuleDto.getBatteryId());
            VehicleWarnDto vehicleWarnDto = vehicleWarnDtoCarIdMap.get(vehicleWarnRuleDto.getCarId());
            if (battery != null) {
                vehicleWarnRuleDto.setBatteryType(battery.getBatteryType());
            }
            if (vehicleWarnDto != null) {
                vehicleWarnRuleDto.setSignal(vehicleWarnDto.getSignal());
                vehicleWarnRuleDto.setWarnId(vehicleWarnDto.getWarnId());
            }
        }


        // 3. 使用Redis缓存，如果Redis有则从Redis取,没有则从DataBase取。
        startTime = System.currentTimeMillis();
        List<WarnRule> warnRuleList = getWarnRulesFromRedisOrDb(warnBatteryIds);
        endTime = System.currentTimeMillis();
        System.out.println("Time taken by getWarnRulesFromRedisOrDb: " + (endTime - startTime) + " ms");

        // 4. 开始对电流、电压进行预警。
        Map<String, Map<String, List<WarnRule>>> resultMap = warnRuleList.stream()
                .collect(Collectors.groupingBy(
                        WarnRule::getBatteryId,
                        Collectors.groupingBy(WarnRule::getRuleId)
                ));

        startTime = System.currentTimeMillis();
        List<VehicleWarnVo> vehicleWarnVoList = batteryWarn(vehicleWarnRuleDtoList, resultMap); // 创建一个返回前端的list
        endTime = System.currentTimeMillis();
        System.out.println("Time taken by batteryWarn: " + (endTime - startTime) + " ms");

        return new BaseResult<>(ResultConstant.SUCCESS_CODE, "ok", vehicleWarnVoList);
    }


    /**
     * 取出电池的预警规则，如果Redis有从redis里面取，如果Redis没有再从mysql里面取。
     *
     * @param warnBatteryIds 所有需要预警的电池的ID
     * @return warnRuleList 从Redis和数据库中取出需预警电池的所有规则
     * @throws JsonProcessingException Jackson解析Json失败
     * @Author zhuzhe1018
     * @date 2024/6/17
     * @Version 1.0
     */
    public List<WarnRule> getWarnRulesFromRedisOrDb(List<String> warnBatteryIds) throws JsonProcessingException {
        List<WarnRule> warnRuleList = new ArrayList<>();
        List<String> batteryIdsToGetFromDb = new ArrayList<>();
        int count = 0;
        //3.1 从Redis中获取数据
        for (String batteryId : warnBatteryIds) {
            //获取key为batteryId的值
            String json = redisUtil.getString(batteryId);
            if (json != null) {
                count++;
                List<WarnRule> warnRulesRedisList = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, WarnRule.class));
                if (warnRulesRedisList != null && !warnRulesRedisList.isEmpty()) {
                    warnRuleList.addAll(warnRulesRedisList);
                } else {
                    batteryIdsToGetFromDb.add(batteryId);
                }
            } else {
                batteryIdsToGetFromDb.add(batteryId);
            }
        }
//        System.out.println("Redis命中条数：  "+count);
        //3.2 从MySQL中获取数据并存入Redis
        if (!batteryIdsToGetFromDb.isEmpty()) {
//            System.out.println("Redis未命中： "+batteryIdsToGetFromDb.size());
            List<WarnRule> warnRulesDataBaseList = warnRuleMapper.selectAllWarnRuleByBatteryIds(batteryIdsToGetFromDb);
            if (warnRulesDataBaseList != null && !warnRulesDataBaseList.isEmpty()) {
                // 按照 batteryId 分组
                Map<String, List<WarnRule>> dbWarnRuleMap = warnRulesDataBaseList.stream()
                        .collect(Collectors.groupingBy(WarnRule::getBatteryId));
                for (Map.Entry<String, List<WarnRule>> entry : dbWarnRuleMap.entrySet()) {
                    String batteryId = entry.getKey();
                    List<WarnRule> rules = entry.getValue();
//                    this.saveWarnRulesToRedis(batteryId,rules);
                    // 将数据存入Redis，设置过期时间
                    redisUtil.setWithExpiration(batteryId, objectMapper.writeValueAsString(rules), RedisConstant.WARN_RULE_EXPIRE_TIME, RedisConstant.WARN_RULE_EXPIRE_UNIT);
                }
                //将数据库中存储的规则存入list
                warnRuleList.addAll(warnRulesDataBaseList);
            }
        }
        return warnRuleList;
    }

    /**
     * @param vehicleWarnRuleDtoList 包含前端传入的车辆信息，还包括了batteryType和batteryId
     * @param resultMap              包含需要预警电池的所有规则，key为batteryId,value为一个map(规则编号作为Key,所有规则的 List<WarnRule>>作为value)
     * @return vehicleWarnVoList 返回所有预警车辆的信息List
     * @throws JsonProcessingException Jackson解析Json失败
     * @Author zhuzhe1018
     * @date 2024/6/17
     * @Version 1.0
     */
    public List<VehicleWarnVo> batteryWarn(List<VehicleWarnRuleDto> vehicleWarnRuleDtoList, Map<String, Map<String, List<WarnRule>>> resultMap) throws JsonProcessingException {
        List<VehicleWarnVo> vehicleWarnVoList = new ArrayList<>();
        //循环 需要预警的车辆信息
        for (VehicleWarnRuleDto vehicleWarnDto : vehicleWarnRuleDtoList) {
            Integer warnId = vehicleWarnDto.getWarnId();
            String batteryId = vehicleWarnDto.getBatteryId();
            Map<String, List<WarnRule>> warnRuleMap = resultMap.get(batteryId);
            //将传入的signal转化为map,以便能够获取数据
            Map<String, Double> signalMap = objectMapper.readValue(vehicleWarnDto.getSignal(), new TypeReference<Map<String, Double>>() {
            });
            Double mx = signalMap.get("Mx");
            Double mi = signalMap.get("Mi");
            Double ii = signalMap.get("Ii");
            Double ix = signalMap.get("Ix");
            //因为warnId为空则即进行电压预警也进行电流预警，为1则进行电压预警，为2则进行电流预警。
            //如果warnId为空 或者 为1，则进行电压预警。
            if ((warnId == null || warnId == 1) && mx != null && mi != null) {
                List<WarnRule> warnRuleList = warnRuleMap.get("1");
                double differenceVoltageDouble = mx - mi;
                BigDecimal differenceVoltageDecimal = BigDecimal.valueOf(differenceVoltageDouble);
                VehicleWarnVo vehicleWarnVo = ruleJudgment(differenceVoltageDecimal, warnRuleList, vehicleWarnDto);
                if (vehicleWarnVo != null) {
                    vehicleWarnVoList.add(vehicleWarnVo);
                }
            }
            //如果warnId为空 或者 为2，则进行电流预警。
            if ((warnId == null || warnId == 2) && ii != null && ix != null) {
                List<WarnRule> warnRuleList = warnRuleMap.get("2");
                double differenceCurrentDouble = ix - ii;
                BigDecimal differenceCurrentDecimal = BigDecimal.valueOf(differenceCurrentDouble);
                VehicleWarnVo vehicleWarnVo = ruleJudgment(differenceCurrentDecimal, warnRuleList, vehicleWarnDto);
                if (vehicleWarnVo != null) {
                    vehicleWarnVoList.add(vehicleWarnVo);
                }
            }
        }
        return vehicleWarnVoList;
    }

    /**
     * 对传入的预警规则和电流差（电压差）进行规则判断，返回预警相关信息，例如预警等级（0,1,2）等。
     *
     * @param difference         传入的差值（电流差、电压差）
     * @param warnRuleList       该电池的电流预警规则或者电压预警规则
     * @param vehicleWarnRuleDto 该车辆及电池的所有信息。
     * @return VehicleWarnVo 返回该车辆电池预警信息，warnLevel等
     * @Author zhuzhe1018
     * @date 2024/6/17
     * @Version 1.0
     */
    public VehicleWarnVo ruleJudgment(BigDecimal difference, List<WarnRule> warnRuleList, VehicleWarnRuleDto vehicleWarnRuleDto) {
        //循环该电池的规则
        for (WarnRule warnRule : warnRuleList) {
            Integer warnLevel = -1;
            BigDecimal lowerLimit = warnRule.getLowerLimit();
            BigDecimal upperLimit = warnRule.getUpperLimit();
            //如果在该规则范围内，保存该规则的预警等级warnLevel
            if ((lowerLimit == null && upperLimit != null && difference.compareTo(upperLimit) > 0) ||
                    (upperLimit == null && lowerLimit != null && difference.compareTo(lowerLimit) < 0) ||
                    (lowerLimit != null && upperLimit != null && difference.compareTo(lowerLimit) > 0 && difference.compareTo(upperLimit) < 0)) {
                warnLevel = warnRule.getWarnLevel();
            }
            if (warnLevel != -1) {
                VehicleWarnVo vehicleWarnVo = new VehicleWarnVo();
                vehicleWarnVo.setWarnLevel(warnRule.getWarnLevel());
                vehicleWarnVo.setWarnName(warnRule.getRuleName());
                vehicleWarnVo.setVehicleNumber(vehicleWarnRuleDto.getCarId());
                vehicleWarnVo.setBatteryType(vehicleWarnRuleDto.getBatteryType());
                return vehicleWarnVo;
            }
        }
        //如果warnLevel为-1，则证明不需要预警，返回null。
        return null;
    }
}
