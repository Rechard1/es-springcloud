package com.jwell56.security.cloud.service.asset.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.asset.entity.Asset;
import com.jwell56.security.cloud.service.asset.entity.vo.AssetPage;

public interface IAssetService extends IService<Asset>{

	AssetPage detail(Integer assetId);
	
	QueryWrapper<Asset> queryWrapperForAreaUnit(List<Integer> areaIdList, List<Integer> unitIdList,
            QueryWrapper<Asset> assetQueryWrapper);
	
	boolean important(List<Integer> assetIds);

	List<String> getIpListByDeviceType(String deviceType);

	Asset getAssetByIp(String ip);


}
