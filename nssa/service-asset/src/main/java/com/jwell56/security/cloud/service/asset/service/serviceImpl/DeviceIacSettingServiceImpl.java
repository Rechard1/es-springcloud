package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.DeviceIacSetting;
import com.jwell56.security.cloud.service.asset.mapper.DeviceIacSettingMapper;
import com.jwell56.security.cloud.service.asset.service.IDeviceIacSettingService;
import org.springframework.stereotype.Service;

@Service
public class DeviceIacSettingServiceImpl extends ServiceImpl<DeviceIacSettingMapper, DeviceIacSetting> implements IDeviceIacSettingService {
}
