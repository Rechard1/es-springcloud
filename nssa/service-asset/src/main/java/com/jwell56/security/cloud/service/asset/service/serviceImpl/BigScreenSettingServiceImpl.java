package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.asset.service.IBigScreenSettingService;

@Service
public class BigScreenSettingServiceImpl extends ServiceImpl<BaseMapper<BigScreenSetting>, BigScreenSetting> implements IBigScreenSettingService{

}
