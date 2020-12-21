package com.jwell56.security.cloud.service.sso.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.sso.entity.RoleModule;
import com.jwell56.security.cloud.service.sso.entity.RoleModuleVo;
import com.jwell56.security.cloud.service.sso.mapper.RoleModuleMapper;
import com.jwell56.security.cloud.service.sso.service.IRoleModuleService;
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

    @Override
    public List<RoleModuleVo> getRoleModuleVoList(Integer roleId) {
        return roleModuleMapper.getRoleModuleVoList(roleId);
    }
}
