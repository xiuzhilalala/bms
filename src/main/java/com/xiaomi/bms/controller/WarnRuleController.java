package com.xiaomi.bms.controller;

import com.github.pagehelper.PageInfo;
import com.xiaomi.bms.common.BaseResult;
import com.xiaomi.bms.entity.WarnRule;
import com.xiaomi.bms.service.WarnRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Api(tags = "电池信息管理")
@RestController
@RequestMapping("/warnRule")
public class WarnRuleController {

    @Autowired
    private WarnRuleService warnRuleService;

    @ApiOperation("新增预警规则")
    @PostMapping(value = "/insertWarnRule")
    public BaseResult<T> insertWarnRule(@RequestBody List<WarnRule> warnRuleList) {
        return warnRuleService.insertWarnRule(warnRuleList);
    }

    @ApiOperation("删除预警规则")
    @PostMapping(value = "/deleteWarnRule")
    public BaseResult<T> deleteWarnRule(@RequestBody List<WarnRule> warnRuleList) {
        return warnRuleService.deleteWarnRule(warnRuleList);
    }

    @ApiOperation("更新预警规则")
    @PostMapping(value = "/updateWarnRule")
    public BaseResult<T> updateWarnRule(@RequestBody WarnRule warnRule) {
        return warnRuleService.updateWarnRule(warnRule);
    }
    
    @ApiOperation("查询预警规则")
    @PostMapping(value = "/selectWarnRule")
    public BaseResult<PageInfo<WarnRule>> selectWarnRule(@RequestBody WarnRule warnRule, @Param("page") Integer page, @Param("size") Integer size) {
        return warnRuleService.selectWarnRule(warnRule,page,size);
    }

}
