package com.jwell56.security.cloud.service.asset.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwell56.security.cloud.service.asset.entity.SysDeviceMonitorUnit;

public interface SysDeviceMonitorUnitMapper  extends BaseMapper<SysDeviceMonitorUnit> {

	List<Integer> getUnitIdList(Integer probeId);
}
