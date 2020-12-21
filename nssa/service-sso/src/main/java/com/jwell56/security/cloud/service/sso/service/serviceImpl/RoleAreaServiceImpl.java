package com.jwell56.security.cloud.service.sso.service.serviceImpl;

import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.sso.entity.RoleArea;
import com.jwell56.security.cloud.service.sso.mapper.RoleAreaMapper;
import com.jwell56.security.cloud.service.sso.service.IRoleAreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色网络区域表 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
@Service
public class RoleAreaServiceImpl extends ServiceImpl<RoleAreaMapper, RoleArea> implements IRoleAreaService {

    @Override
    public List<RoleArea> roleAreaCache() {
        List<RoleArea> roleAreaList = (List<RoleArea>) CommonCachePool.getData("roleAreaCache");
        if (roleAreaList == null) {
            roleAreaList = this.list(null);
            CommonCachePool.setData("roleAreaCache", roleAreaList);
        }
        return roleAreaList;
    }
}
