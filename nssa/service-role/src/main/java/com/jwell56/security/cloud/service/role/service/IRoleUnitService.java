package com.jwell56.security.cloud.service.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.role.entity.RoleUnit;

import java.util.List;

/**
 * <p>
 * 角色单位表 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
public interface IRoleUnitService extends IService<RoleUnit> {
    List<RoleUnit> roleUnitCache();
    List<Integer> getRoleUnitList(Integer roleId);
    
    List<Integer> getRoleUnitIds(Integer roleId, Integer enterpriseId);
}
