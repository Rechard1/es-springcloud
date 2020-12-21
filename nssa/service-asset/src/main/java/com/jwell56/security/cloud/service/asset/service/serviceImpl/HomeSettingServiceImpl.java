package com.jwell56.security.cloud.service.asset.service.serviceImpl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.HomeSetting;
import com.jwell56.security.cloud.service.asset.mapper.HomeSettingMapper;
import com.jwell56.security.cloud.service.asset.service.IHomeSettingService;
import org.springframework.stereotype.Service;

@Service
public class HomeSettingServiceImpl extends ServiceImpl<HomeSettingMapper, HomeSetting> implements IHomeSettingService {

}
