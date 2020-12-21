package com.jwell56.security.cloud.service.netstruct.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jwell56.security.cloud.common.ResultObject;

@FeignClient("service-asset")
public interface IAssetService {

	@GetMapping("/device/getDeviceById")
	public ResultObject getDeviceById(@RequestParam("deviceId") Integer deviceId);
	
	@GetMapping("/asset/isDelete")
	public ResultObject isDelete(@RequestParam("areaId") Integer areaId, @RequestParam("unitId") Integer unitId);
}
