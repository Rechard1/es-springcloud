package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.common.cache.CommonDataCache;
import com.jwell56.security.cloud.service.asset.entity.Asset;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.entity.vo.AssetPage;
import com.jwell56.security.cloud.service.asset.mapper.AssetMapper;
import com.jwell56.security.cloud.service.asset.service.IAssetService;
import com.jwell56.security.cloud.service.asset.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;

@Service
public class AssetServiceImpl extends ServiceImpl<BaseMapper<Asset>, Asset> implements IAssetService{
	
//	@Autowired
//	private RoleAreaComponent roleAreaComponent;
//	
//	@Autowired
//	private RoleUnitComponent roleUnitComponent;
	
	@Autowired
	private NetStructComponent netStructComponent;
	
	@Autowired
	private AssetMapper assetMapper;
	
	@Override
	public AssetPage detail(Integer assetId) {
//		Map<String, Object> resMap = new HashMap<String, Object>(8);
		Asset assetDetail = this.getById(assetId);
//		if(!StringUtils.isEmpty(assetDetail.getExpandInfo())) {
//			Map<String, String> expandinfo = getExpandInfo(assetDetail.getExpandInfo());
//			resMap.put("expandInfo", expandinfo);
//		}
		AssetPage resAsset = new AssetPage();
		BeanUtils.copyProperties(assetDetail, resAsset);
		if(assetDetail.getUnitId() != null || assetDetail.getUnitId() != 0) {
			resAsset.setUnitName(netStructComponent.getUnitName(assetDetail.getUnitId()));
		}
		if(assetDetail.getAreaId() != null || assetDetail.getAreaId() != 0) {
			resAsset.setAreaName(netStructComponent.getAreaName(assetDetail.getAreaId()));
		}
//		resMap.put("assetDetail", resAsset);
		
		return resAsset;
	}
	
	@Override
	public QueryWrapper<Asset> queryWrapperForAreaUnit(List<Integer> areaIdList, List<Integer> unitIdList,
			QueryWrapper<Asset> assetQueryWrapper) {
		try {
            if (assetQueryWrapper == null) {
                assetQueryWrapper = new QueryWrapper<>();
            }
            User userInfo = ThreadLocalUtil.getInstance().getUserInfo();

            //权限控制
//            List<Integer> roleAreaIdList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
//            List<Integer> roleUnitIdList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
//            //当无权限时,返回，不进行查询
//            if (roleAreaIdList == null || roleAreaIdList.isEmpty() || roleUnitIdList == null || roleUnitIdList.isEmpty()) {
//            	assetQueryWrapper.apply("1<>1");
//            } else {
//            	assetQueryWrapper.lambda().in(Asset::getAreaId, roleAreaIdList);
//            	assetQueryWrapper.lambda().in(Asset::getUnitId, roleUnitIdList);
//            }
            //单位和区域至少有一个是有效筛选条件才进行筛选，否则不筛选
            if (areaIdList != null && !areaIdList.isEmpty()) {
                assetQueryWrapper.lambda().in(Asset::getAreaId, areaIdList);
            }
            if (unitIdList != null && !unitIdList.isEmpty()) {
                assetQueryWrapper.lambda().in(Asset::getUnitId, unitIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assetQueryWrapper;
	}
	
	//传入格式：xxxx:xxxx;xxxx:xxxxx
	private Map<String, String> getExpandInfo(String expandInfo){
		Map<String, String> resMap = new HashMap<String, String>(32);
		String[] expandInfos = expandInfo.split(";");
		for(String info : expandInfos) {
			String[] infos = info.split(":");
			resMap.put(infos[0], infos[1]);
		}
		return resMap;
	}

	@Override
	public boolean important(List<Integer> assetIds) {
		return assetMapper.important(assetIds);
	}


	@Override
	public List<String> getIpListByDeviceType(String deviceType) {
		if (deviceType == null || deviceType.isEmpty() || deviceType.equals("全部")) {
			return null;
		}
		List<String> ipList = getAssetIpListByDeviceTypeCache().get(deviceType);
		if (ipList == null || ipList.isEmpty()) {
			List<String> noneIpList = new ArrayList<>();
			noneIpList.add("no search value");
			return noneIpList;
		} else {
			return ipList;
		}
	}

	private Map<String, List<String>> getAssetIpListByDeviceTypeCache() {
		Map<String, List<String>> ipMap = new HashMap<>();
		String cacheKey = "asset-ip-list-from-device-type-map";
		if (CommonCachePool.getData(cacheKey) == null) {
			List<Asset> assetList = this.getAssetCache();
			for (Asset asset : assetList) {
				String type = null;
				if (asset.getType() != null &&
						(asset.getType().equals(Asset.TYPE_SERVER) || asset.getType().equals(Asset.TYPE_NETDEV) ||
								asset.getType().equals(Asset.TYPE_SECURITY) || asset.getType().equals(Asset.TYPE_COMPUTER))) {
					type = asset.getType();
				}
				if (type != null && asset.getIp() != null) {
					ipMap.computeIfAbsent(type, k -> new ArrayList<>());
					ipMap.get(type).add(asset.getIp());
				}
			}
			CommonCachePool.setData(cacheKey, ipMap);
		} else {
			ipMap = (Map<String, List<String>>) CommonCachePool.getData(cacheKey);
		}
		return ipMap;
	}

	private List<Asset> getAssetCache() {
		List<Asset> assetList;
		CommonCachePool cachePool = new CommonCachePool();
		String cacheKey = "asset-list";
		CommonDataCache dataCache = cachePool.get(cacheKey);
		if (dataCache == null) {
			assetList = this.list(null);
			dataCache = new CommonDataCache(cacheKey, assetList);
			cachePool.add(dataCache);
		} else {
			assetList = (List<Asset>) dataCache.getData();
		}
		return assetList;
	}


	private Map<String, Asset> getAssetIpMap() {
		List<Asset> assetList = this.getAssetCache();
		Map<String, Asset> assetIpMap = new HashMap<>();
		CommonCachePool cachePool = new CommonCachePool();
		String cacheKey = "asset-ip-map";
		CommonDataCache dataCache = cachePool.get(cacheKey);
		if (dataCache == null) {
			for (Asset asset : assetList) {
				assetIpMap.put(asset.getIp(), asset);
			}
			dataCache = new CommonDataCache(cacheKey, assetIpMap);
			cachePool.add(dataCache);
		} else {
			assetIpMap = (Map<String, Asset>) dataCache.getData();
		}
		return assetIpMap;
	}

	@Override
	public Asset getAssetByIp(String ip) {
		return this.getAssetIpMap().get(ip);
	}

}
