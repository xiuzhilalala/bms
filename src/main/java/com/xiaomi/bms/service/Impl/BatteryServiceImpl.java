package com.xiaomi.bms.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.bms.entity.Battery;
import com.xiaomi.bms.mapper.BatteryMapper;
import com.xiaomi.bms.service.BatteryService;
import org.springframework.stereotype.Service;

/**
 * BatteryService 接口的实现类，继承自 ServiceImpl。
 * 处理电池信息管理相关的业务逻辑
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Service
public class BatteryServiceImpl extends ServiceImpl<BatteryMapper, Battery> implements BatteryService {

}
