package com.jwell56.security.cloud.service.ids.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.ids.annotation.Validation;
import com.jwell56.security.cloud.service.ids.common.Pages;
import com.jwell56.security.cloud.service.ids.common.Times;
import com.jwell56.security.cloud.service.ids.common.Trends;
import com.jwell56.security.cloud.service.ids.entity.vo.DistinctVo;
import com.jwell56.security.cloud.service.ids.entity.vo.IdVo;
import com.jwell56.security.cloud.service.ids.service.Service;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @author wsg
 * @since 2020/12/2
 */
public class IdsBaseController<T> {
    Service<T> service;

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public ResultObject<Page<T>> page(T t, Times times, Pages pages) {
        return ResultObject.data(service.searchPage(t, times, pages));
    }

    @ApiOperation("详情")
    @GetMapping("/detail")
    public ResultObject detail(IdVo idVo) {
        return ResultObject.data(service.searchDetail(idVo.getId()));
    }

    @ApiOperation("字段去重列表")
    @GetMapping("/distinct")
    public ResultObject<List<String>> distinct(DistinctVo distinctVo) {
        return ResultObject.data(service.searchDistinct(distinctVo.getField()));
    }

    @ApiOperation("趋势图")
    @GetMapping("/trend")
    public ResultObject<Map<String, Map<String, Object>>> trend(Trends trends, Times times) {
        return ResultObject.data(service.searchTrend(trends, times));
    }
}
