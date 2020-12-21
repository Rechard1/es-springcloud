package com.jwell56.security.cloud.service.role.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.role.entity.Role;

public interface IRoleService extends IService<Role>{

	void saveRole(String unitList, String areaList, String moduleList, Integer roleId);
	
	Map<String, Object> detail(Integer roleId);
}
