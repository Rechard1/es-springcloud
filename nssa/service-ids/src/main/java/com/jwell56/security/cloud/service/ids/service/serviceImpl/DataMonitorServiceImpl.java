package com.jwell56.security.cloud.service.ids.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.ids.entity.DataMonitor;
import com.jwell56.security.cloud.service.ids.service.IDataMonitorService;

@Service
public class DataMonitorServiceImpl extends ServiceImpl<BaseMapper<DataMonitor>, DataMonitor> implements IDataMonitorService{

}
