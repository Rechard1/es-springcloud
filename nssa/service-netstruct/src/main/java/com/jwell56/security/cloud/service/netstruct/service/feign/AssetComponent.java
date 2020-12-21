package com.jwell56.security.cloud.service.netstruct.service.feign;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.entity.SysDevice;

@Component
public class AssetComponent {

	@Autowired
	private IAssetService assetService;
	
	public String getAssetById(int deviceId) {
        ResultObject resultObject = assetService.getDeviceById(deviceId);
        Map asset = (Map) resultObject.getData();
        return (String) asset.get("name");
    }
	
	public Map<String, Object> isDelete(Integer areaId, Integer unitId){
		ResultObject resultObject = assetService.isDelete(areaId, unitId);
        Map isDelete = (Map) resultObject.getData();
        return isDelete;
	}
}
