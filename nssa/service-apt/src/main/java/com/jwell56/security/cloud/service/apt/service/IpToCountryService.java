package com.jwell56.security.cloud.service.apt.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.apt.entity.IpToCountry;

public interface IpToCountryService extends IService<IpToCountry> {
    public String getCounty(String code);
}
