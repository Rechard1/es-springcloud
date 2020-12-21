package com.jwell56.security.cloud.service.role.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.role.entity.Enterprise;
import com.jwell56.security.cloud.service.role.entity.Role;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.service.IEnterpriseService;
import com.jwell56.security.cloud.service.role.service.IRoleService;
import com.jwell56.security.cloud.service.role.service.ISysUserService;

@Service
public class EnterpriseServiceImpl extends ServiceImpl<BaseMapper<Enterprise>, Enterprise> implements IEnterpriseService{

	@Autowired
	private ISysUserService userService;
	
	@Autowired
	private IRoleService roleService;
	
	@Override
	public void delete(Integer enterpriseId) {
		QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>();
		userQueryWrapper.lambda().eq(SysUser :: getEnterpriseId, enterpriseId);
		userService.remove(userQueryWrapper);
		QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
		roleQueryWrapper.lambda().eq(Role :: getEnterpriseId, enterpriseId);
		roleService.remove(roleQueryWrapper);
		this.removeById(enterpriseId);
	}

}
