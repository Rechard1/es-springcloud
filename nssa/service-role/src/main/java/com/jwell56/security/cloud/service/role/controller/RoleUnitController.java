package com.jwell56.security.cloud.service.role.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.role.service.IRoleUnitService;

/**
 * <p>
 * 角色单位表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
@RestController
@RequestMapping("/role/unit")
public class RoleUnitController {

	@Autowired
	private IRoleUnitService roleUnitService;
	
	@GetMapping("/getRoleUnitList")
	public ResultObject getRoleUnitList(Integer roleId) {
		return ResultObject.data(roleUnitService.getRoleUnitList(roleId));
	}
	
	@GetMapping("/getRoleUnitIds")
	public ResultObject getRoleUnitIds(Integer roleId, Integer enterpriseId) {
		return ResultObject.data(roleUnitService.getRoleUnitIds(roleId, enterpriseId));
	}
}
