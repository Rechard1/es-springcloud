package com.jwell56.security.cloud.service.sso.service;

import com.jwell56.security.cloud.service.sso.entity.RoleArea;
import com.jwell56.security.cloud.service.sso.entity.RoleUnit;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
