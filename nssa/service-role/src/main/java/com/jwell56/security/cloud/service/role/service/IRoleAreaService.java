package com.jwell56.security.cloud.service.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.role.entity.RoleArea;

import java.util.List;

/**
 * <p>
 * 角色网络区域表 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
public interface IRoleAreaService extends IService<RoleArea> {
    List<RoleArea> roleAreaCache();
    List<Integer> getRoleAreaIdList(Integer roleId);
    
    List<Integer> getRoleAreaIds(Integer roleId, Integer enterpriseId);
}
