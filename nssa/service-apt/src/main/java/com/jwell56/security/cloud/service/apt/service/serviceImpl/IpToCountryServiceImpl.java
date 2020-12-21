package com.jwell56.security.cloud.service.apt.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.apt.entity.IpToCountry;
import com.jwell56.security.cloud.service.apt.mapper.IpToCountyMapper;
import com.jwell56.security.cloud.service.apt.service.IpToCountryService;
import com.jwell56.security.cloud.service.apt.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IpToCountryServiceImpl  extends ServiceImpl<IpToCountyMapper, IpToCountry> implements IpToCountryService {

    @Autowired
    private IpToCountyMapper ipToCountyMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getCounty(String code) {
        Map<String, String> map = new HashMap<>();
        String cacheKey = "iptocounty";
        if (redisUtil.get(cacheKey) == null) {
            QueryWrapper<IpToCountry> queryWrapper = new QueryWrapper<IpToCountry>();
            queryWrapper.select("areacode","country");
            queryWrapper.groupBy("areacode");
            List<IpToCountry> resultList = ipToCountyMapper.selectList(queryWrapper);
            for(IpToCountry objectMap : resultList){
                map.put(objectMap.getAreacode(), objectMap.getCountry());
            }
            redisUtil.set(cacheKey, map, 60*60*24*30);
        } else {
            map = (Map<String, String>) redisUtil.get(cacheKey);
        }
        return map.get(code);
    }
}
