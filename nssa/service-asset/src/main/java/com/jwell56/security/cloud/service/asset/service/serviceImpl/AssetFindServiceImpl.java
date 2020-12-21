package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.AssetFind;
import com.jwell56.security.cloud.service.asset.service.IAssetFindService;

@Service
public class AssetFindServiceImpl extends ServiceImpl<BaseMapper<AssetFind>, AssetFind> implements IAssetFindService{

}
