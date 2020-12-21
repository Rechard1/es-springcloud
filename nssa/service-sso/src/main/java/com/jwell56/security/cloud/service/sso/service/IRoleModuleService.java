package com.jwell56.security.cloud.service.sso.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.sso.entity.RoleModule;
import com.jwell56.security.cloud.service.sso.entity.RoleModuleVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RonnieXu
 * @since 2019-04-15
 */
public interface IRoleModuleService extends IService<RoleModule> {

    /**
     * 根据角色查询用户的功能列表
     * @param roleId
     * @return
     */
    List<RoleModuleVo> getRoleModuleVoList(Integer roleId);
}
