package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.AssetDevice;
import com.jwell56.security.cloud.service.asset.mapper.AssetDeviceMapper;
import com.jwell56.security.cloud.service.asset.service.IAssetDeviceService;

@Service
public class AssetDeviceServiceImpl  extends ServiceImpl<AssetDeviceMapper, AssetDevice> implements IAssetDeviceService {

}
