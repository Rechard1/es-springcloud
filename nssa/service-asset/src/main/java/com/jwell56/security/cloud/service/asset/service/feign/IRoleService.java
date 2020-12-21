package com.jwell56.security.cloud.service.asset.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jwell56.security.cloud.common.ResultObject;

@FeignClient("service-role")
public interface IRoleService {

	@GetMapping("/role/unit/getRoleUnitList")
	public ResultObject getRoleUnitList(@RequestParam("roleId") Integer roleId);
	
	@GetMapping("/role/area/getRoleAreaList")
	public ResultObject getRoleAreaList(@RequestParam("roleId") Integer roleId);

	@GetMapping("/user/getUserById")
	public ResultObject getUserById(@RequestParam("userId") Integer userId);
	
	@GetMapping("/role/unit/getRoleUnitIds")
	public ResultObject getRoleUnitIds(@RequestParam("roleId") Integer roleId,@RequestParam("enterpriseId") Integer enterpriseId);
	
	@GetMapping("/role/area/getRoleAreaIds")
	public ResultObject getRoleAreaIds(@RequestParam("roleId") Integer roleId,@RequestParam("enterpriseId") Integer enterpriseId);
}
