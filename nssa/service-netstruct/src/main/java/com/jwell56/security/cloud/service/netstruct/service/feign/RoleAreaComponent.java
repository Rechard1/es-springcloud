package com.jwell56.security.cloud.service.netstruct.service.feign;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.common.ResultObject;

@Component
public class RoleAreaComponent {

	@Autowired
	private IRoleService roleAreaService;
	
//	public List<Integer> areaList(Integer roleId) {
//		ResultObject res = roleAreaService.getRoleAreaList(roleId);
//		List<Integer> resAreaList = (List<Integer>) res.getData();
//		return resAreaList;
//	}
	
	public List<Integer> areaList(Integer roleId, Integer enterpriseId) {
		ResultObject res = roleAreaService.getRoleAreaIds(roleId, enterpriseId);
		List<Integer> resAreaList = (List<Integer>) res.getData();
		return resAreaList;
	}
}
