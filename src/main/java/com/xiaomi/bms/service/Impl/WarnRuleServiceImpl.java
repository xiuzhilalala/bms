package com.xiaomi.bms.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaomi.bms.common.BaseResult;
import com.xiaomi.bms.entity.WarnRule;
import com.xiaomi.bms.mapper.WarnRuleMapper;
import com.xiaomi.bms.service.WarnRuleService;
import com.xiaomi.bms.utils.SnowFlakeUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * WarnRule服务实现类
 * 处理电池预警规则相关的业务逻辑
 * @Author zhuzhe1018
 * @Date 2024/6/16
 */
@Service
public class WarnRuleServiceImpl extends ServiceImpl<WarnRuleMapper, WarnRule> implements WarnRuleService {

    @Autowired
    private WarnRuleMapper warnRuleMapper;

    /**
     * 插入WarnRule记录
     *
     * @param warnRuleList WarnRule对象列表
     * @return BaseResult<T>
     * @Author zhuzhe1018
     * @Date 2024/6/16
     */
    @Override
    @Transactional
    public BaseResult<T> insertWarnRule(List<WarnRule> warnRuleList) {
        System.out.println(warnRuleList);
        SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(1);
        try {
            for (WarnRule warnRule : warnRuleList) {
                long id = snowFlakeUtils.nextId();
                System.out.println(id);
                warnRule.setId(String.valueOf(id));
            }
            boolean isInsert = this.saveOrUpdateBatch(warnRuleList);

            if (isInsert) {
                return BaseResult.isSuccess();
            } else {
                return BaseResult.isError("insert warnRule failed");
            }
        } catch (Exception e) {
            System.out.println(e);
            return BaseResult.isError(e.getMessage());
        }
    }

    /**
     * 删除WarnRule记录
     *
     * @param warnRuleList WarnRule对象列表
     * @return BaseResult<T>
     * @Author zhuzhe1018
     * @Date 2024/6/16
     */
    @Override
    @Transactional
    public BaseResult<T> deleteWarnRule(List<WarnRule> warnRuleList) {
        try {
            // 批量删除
            int deleteCount = warnRuleMapper.deleteWarnRuleByIds(warnRuleList);
            if (deleteCount > 0) {
                return BaseResult.isSuccess();
            } else {
                return BaseResult.isError("Failed to delete WarnRules");
            }
        } catch (Exception e) {
            return BaseResult.isError(e.getMessage());
        }
    }

    /**
     * 更新WarnRule记录
     *
     * @param warnRule WarnRule对象
     * @return BaseResult<T>
     * @Author zhuzhe1018
     * @Date 2024/6/16
     */
    @Override
    @Transactional
    public BaseResult<T> updateWarnRule(WarnRule warnRule) {
        try {
            int updateCount = warnRuleMapper.updateBatchById(warnRule);
            if (updateCount > 0) {
                return BaseResult.isSuccess();
            } else {
                return BaseResult.isError("Failed to edit WarnRules");
            }
        } catch (Exception e) {
            return BaseResult.isError("An error occurred: " + e.getMessage());
        }
    }

    /**
     * 分页查询WarnRule记录
     *
     * @param warnRule WarnRule对象
     * @param page 页码
     * @param size 每页大小
     * @return BaseResult<PageInfo<WarnRule>>
     * @Author zhuzhe1018
     * @Date 2024/6/16
     */
    @Override
    public BaseResult<PageInfo<WarnRule>> selectWarnRule(WarnRule warnRule, Integer page, Integer size) {
        try {
            if (page == null) {
                page = 1;
            }
            if (size == null) {
                size = 10;
            }
            PageHelper.startPage(page, size);
            List<WarnRule> result = warnRuleMapper.selectWarnRule(warnRule);
            PageInfo<WarnRule> pageInfo = new PageInfo<>(result);
            return BaseResult.isSuccess(pageInfo);
        } catch (Exception e) {
            return BaseResult.isError("An error occurred: " + e.getMessage());
        }
    }
}
