package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.AssetFindApplication;
import com.jwell56.security.cloud.service.asset.service.IAssetFindApplicationService;

@Service
public class AssetFindApplicationServiceImpl extends ServiceImpl<BaseMapper<AssetFindApplication>, AssetFindApplication> implements IAssetFindApplicationService{

}
