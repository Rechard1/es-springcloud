package com.jwell56.security.cloud.service.asset.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwell56.security.cloud.service.asset.entity.SysDeviceMonitorArea;

public interface SysDeviceMonitorAreaMapper extends BaseMapper<SysDeviceMonitorArea> {

	List<Integer> getAreaIdList(Integer probeId);
}
