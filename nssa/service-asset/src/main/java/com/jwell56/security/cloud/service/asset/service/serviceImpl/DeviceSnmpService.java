package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.DeviceSnmp;
import com.jwell56.security.cloud.service.asset.service.IDeviceSnmpservice;
import org.springframework.stereotype.Service;

@Service
public class DeviceSnmpService extends ServiceImpl<BaseMapper<DeviceSnmp>, DeviceSnmp> implements IDeviceSnmpservice {

}
