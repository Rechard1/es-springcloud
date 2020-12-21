package com.jwell56.security.cloud.service.role.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.role.entity.Module;
import com.jwell56.security.cloud.service.role.entity.RoleModule;
import com.jwell56.security.cloud.service.role.entity.RoleModuleVo;
import com.jwell56.security.cloud.service.role.mapper.RoleModuleMapper;
import com.jwell56.security.cloud.service.role.service.IRoleModuleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author RonnieXu
 * @since 2019-04-15
 */
@Service
public class RoleModuleServiceImpl extends ServiceImpl<RoleModuleMapper, RoleModule> implements IRoleModuleService {

    @Autowired
    RoleModuleMapper roleModuleMapper;

	public List<Module> getRoleModuleVoList(Integer roleId) {
		 return roleModuleMapper.getRoleModuleVoList(roleId);
	}

    
}
