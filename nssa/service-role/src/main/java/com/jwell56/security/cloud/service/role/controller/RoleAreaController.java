package com.jwell56.security.cloud.service.role.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.role.service.IRoleAreaService;

/**
 * <p>
 * 角色网络区域表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
@RestController
@RequestMapping("/role/area")
public class RoleAreaController {

	@Autowired
	private IRoleAreaService roleAreaService;
	
	@GetMapping("/getRoleAreaList")
	public ResultObject getRoleAreaList(Integer roleId) {
		return ResultObject.data(roleAreaService.getRoleAreaIdList(roleId));
	}
	
	@GetMapping("/getRoleAreaIds")
	public ResultObject getRoleAreaIds(Integer roleId, Integer enterpriseId) {
		return ResultObject.data(roleAreaService.getRoleAreaIds(roleId, enterpriseId));
	}
}
