package com.xiaomi.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaomi.bms.common.BaseResult;
import com.xiaomi.bms.entity.WarnRule;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
public interface WarnRuleService extends IService<WarnRule> {
    BaseResult<T> insertWarnRule(List<WarnRule> warnRuleList);

    BaseResult<T> updateWarnRule(WarnRule warnRule);
    BaseResult<T> deleteWarnRule(List<WarnRule> warnRuleList);

    BaseResult<PageInfo<WarnRule>> selectWarnRule(WarnRule warnRule, Integer page, Integer size);
}
