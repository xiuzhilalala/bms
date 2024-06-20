package com.xiaomi.bms.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.bms.entity.Battery;
import com.xiaomi.bms.mapper.BatteryMapper;
import com.xiaomi.bms.service.BatteryService;
import org.springframework.stereotype.Service;


/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Service
public class BatteryServiceImpl extends ServiceImpl<BatteryMapper, Battery>  implements BatteryService {

}
