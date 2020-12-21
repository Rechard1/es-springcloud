package com.jwell56.security.cloud.service.sso.service.serviceImpl;

import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.sso.entity.RoleArea;
import com.jwell56.security.cloud.service.sso.entity.RoleUnit;
import com.jwell56.security.cloud.service.sso.mapper.RoleUnitMapper;
import com.jwell56.security.cloud.service.sso.service.IRoleUnitService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色单位表 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
@Service
public class RoleUnitServiceImpl extends ServiceImpl<RoleUnitMapper, RoleUnit> implements IRoleUnitService {

    @Override
    public List<RoleUnit> roleUnitCache() {
        List<RoleUnit> roleUnitList = (List<RoleUnit>) CommonCachePool.getData("roleUnitCache");
        if (roleUnitList == null) {
            roleUnitList = this.list(null);
            CommonCachePool.setData("roleAreaCache", roleUnitList);
        }
        return roleUnitList;
    }
}
