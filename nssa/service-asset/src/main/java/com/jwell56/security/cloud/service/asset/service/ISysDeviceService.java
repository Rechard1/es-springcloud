package com.jwell56.security.cloud.service.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.asset.entity.SysDevice;

import java.util.List;

/**
 * <p>
 * 设备状态表 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-04
 */
public interface ISysDeviceService extends IService<SysDevice> {

    public QueryWrapper<SysDevice> queryWrapperForAreaUnit(QueryWrapper<SysDevice> assetQueryWrapper);

}
