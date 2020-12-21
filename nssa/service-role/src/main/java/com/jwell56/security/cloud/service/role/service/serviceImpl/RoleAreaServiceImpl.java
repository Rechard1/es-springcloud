package com.jwell56.security.cloud.service.role.service.serviceImpl;

import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.role.entity.RoleArea;
import com.jwell56.security.cloud.service.role.mapper.RoleAreaMapper;
import com.jwell56.security.cloud.service.role.service.IRoleAreaService;
import com.jwell56.security.cloud.service.role.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.role.utils.RedisUtil;
import com.jwell56.security.cloud.service.role.utils.StringIdsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private NetStructComponent netStructComponent;
	
    @Override
    public List<RoleArea> roleAreaCache() {
        List<RoleArea> roleAreaList = (List<RoleArea>) CommonCachePool.getData("roleAreaCache");
        if (roleAreaList == null) {
            roleAreaList = this.list(null);
            CommonCachePool.setData("roleAreaCache", roleAreaList);
        }
        return roleAreaList;
    }
    
    @Override
    public List<Integer> getRoleAreaIdList(Integer roleId) {
        List<Integer> roleAreaIdList = new ArrayList<>();
        if (roleId != null && roleId != 0) {

//            Map<Integer, List<Integer>> roleAreaIdMap = (Map<Integer, List<Integer>>) CommonCachePool.getData("roleAreaIdMap");
            if (redisUtil.get("roleArea_" + roleId) == null) {
//                roleAreaIdMap = new HashMap<>();
            	QueryWrapper<RoleArea> wrapper = new QueryWrapper<RoleArea>();
            	wrapper.lambda().eq(RoleArea :: getRoleId, roleId);
                List<RoleArea> roleAreaList = this.list(wrapper);
//                List<Integer> idList = new ArrayList<Integer>();
                for (RoleArea roleArea : roleAreaList) {
                	roleAreaIdList.add(roleArea.getAreaId());
//                    roleAreaIdMap.put(roleArea.getRoleId(), idList);
                }
                redisUtil.set("roleArea_" + roleId, StringIdsUtil.StringIds(roleAreaIdList));
            }else {
            	String roleAreaIds = (String) redisUtil.get("roleArea_" + roleId);
            	roleAreaIdList = StringIdsUtil.listIds(roleAreaIds);
            }
        }
        return roleAreaIdList;
    }

	@Override
	public List<Integer> getRoleAreaIds(Integer roleId, Integer enterpriseId) {
		List<Integer> roleAreaIdList = new ArrayList<>();
		
		if(roleId == 0 || roleId == 1) {
			roleAreaIdList = netStructComponent.getAreaByEnterpriseId(enterpriseId);
			return roleAreaIdList;
		}
        if (roleId != null) {

            if (redisUtil.get("roleArea_" + roleId) == null) {
            	QueryWrapper<RoleArea> wrapper = new QueryWrapper<RoleArea>();
            	wrapper.lambda().eq(RoleArea :: getRoleId, roleId);
                List<RoleArea> roleAreaList = this.list(wrapper);
                for (RoleArea roleArea : roleAreaList) {
                	roleAreaIdList.add(roleArea.getAreaId());
                }
                redisUtil.set("roleArea_" + roleId, StringIdsUtil.StringIds(roleAreaIdList));
            }else {
            	String roleAreaIds = (String) redisUtil.get("roleArea_" + roleId);
            	roleAreaIdList = StringIdsUtil.listIds(roleAreaIds);
            }
        }
        return roleAreaIdList;
	}
    
}
