package com.jwell56.security.cloud.service.apt.service.feign;

import com.jwell56.security.cloud.common.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleUnitComponent {

	@Autowired
	private IRoleService roleUnitService;
	
//	public List<Integer> unitList(Integer roleId){
//		ResultObject res = roleUnitService.getRoleUnitList(roleId);
//		List<Integer> resUnitList = (List<Integer>) res.getData();
//		return resUnitList;
//	}
	
	public List<Integer> roleUnitList(Integer roleId, Integer enterpriseId){
		ResultObject res = roleUnitService.getRoleUnitIds(roleId, enterpriseId);
		List<Integer> resUnitList = (List<Integer>) res.getData();
		return resUnitList;
	}
}
