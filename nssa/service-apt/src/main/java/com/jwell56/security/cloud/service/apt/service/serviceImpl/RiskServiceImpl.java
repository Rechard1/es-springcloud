package com.jwell56.security.cloud.service.apt.service.serviceImpl;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.common.entity.Risk;
import com.jwell56.security.cloud.service.apt.mapper.RiskMapper;
import com.jwell56.security.cloud.service.apt.service.RiskService;
import com.jwell56.security.cloud.service.apt.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskServiceImpl  extends ServiceImpl<RiskMapper, Risk> implements RiskService {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RiskMapper riskMapper;

    @Override
    public Risk getRisk(String riskType) {
//        List<Risk> list = new ArrayList<>();
//        String cacheKey = "risk";
//        if (redisUtil.get(cacheKey) == null) {
//            QueryWrapper<Risk> queryWrapper = new QueryWrapper<Risk>();
//            List<Risk> resultList = riskMapper.selectList(queryWrapper);
//            list = resultList;
//            redisUtil.set(cacheKey, resultList, 60*60*24*30);
//        } else {
//            list = (List<Risk>) redisUtil.get(cacheKey);
//        }
//
//
//        for(Risk risk : list ){
//            if(riskType!=null && riskType.equals(risk.getRiskName())){
//                return risk;
//            }
//        }
//        return null;
        QueryWrapper<Risk> queryWrapper = new QueryWrapper<Risk>();
        queryWrapper.lambda().eq(Risk::getRiskName,riskType);
        List<Risk> resultList = this.list(queryWrapper);
        if(resultList != null && !resultList.isEmpty()){
            return resultList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public List<Risk> getRisks(String riskNamaes) {
        JSONArray riskArray = JSONArray.parseArray(riskNamaes);
        QueryWrapper<Risk> queryWrapper = new QueryWrapper<>();
        if (riskArray.size() != 0 || riskNamaes != null || StringUtils.isNotEmpty(riskNamaes)) {
            List<String> riskNameList = riskArray.toJavaList(String.class);
            queryWrapper.lambda().in(Risk::getRiskName, riskNameList);
        }else {
            queryWrapper.lambda().eq(Risk::getRiskId, 0);
        }

        IPage<Risk> page = new Page<>(1, 10);
        IPage<Risk> riskIPage = this.page(page, queryWrapper);

        List<Risk> riskList = riskIPage.getRecords();
        return riskList;
    }
}
