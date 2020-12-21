package com.jwell56.security.cloud.service.role.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.role.entity.RoleUnit;
import com.jwell56.security.cloud.service.role.mapper.RoleUnitMapper;
import com.jwell56.security.cloud.service.role.service.IRoleUnitService;
import com.jwell56.security.cloud.service.role.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.role.utils.RedisUtil;
import com.jwell56.security.cloud.service.role.utils.StringIdsUtil;

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

	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private NetStructComponent netStructComponent;
	
    @Override
    public List<RoleUnit> roleUnitCache() {
        List<RoleUnit> roleUnitList = (List<RoleUnit>) CommonCachePool.getData("roleUnitCache");
        if (roleUnitList == null) {
            roleUnitList = this.list(null);
            CommonCachePool.setData("roleUnitCache", roleUnitList);
        }
        return roleUnitList;
    }
    
    @Override
    public List<Integer> getRoleUnitList(Integer roleId) {
        List<Integer> roleUnitIdList = new ArrayList<>();
        if (roleId != null && roleId != 0) {

//            Map<Integer, List<Integer>> roleUnitIdMap = (Map<Integer, List<Integer>>) CommonCachePool.getData("roleUnitIdMap");
            if (redisUtil.get("roleUnit_" + roleId) == null) {
//                roleUnitIdMap = new HashMap<>();
            	QueryWrapper<RoleUnit> wrapper = new QueryWrapper<RoleUnit>();
            	wrapper.lambda().eq(RoleUnit :: getRoleId, roleId);
                List<RoleUnit> roleUnitList = this.list(wrapper);
                for (RoleUnit roleUnit : roleUnitList) {
//                    List<Integer> idList = roleUnitIdMap.get(roleUnit.getRoleId());
//                    if (idList == null) {
//                        idList = new ArrayList<>();
//                    }
                	roleUnitIdList.add(roleUnit.getUnitId());
//                    roleUnitIdMap.put(roleUnit.getRoleId(), idList);
                }
//                CommonCachePool.setData("roleUnitIdMap", roleUnitIdMap);
                redisUtil.set("roleUnit_" + roleId, StringIdsUtil.StringIds(roleUnitIdList));
            }else {
            	String roleUnitIds = (String) redisUtil.get("roleUnit_" + roleId);
            	roleUnitIdList = StringIdsUtil.listIds(roleUnitIds);
            }

        }
        return roleUnitIdList;
    }

	@Override
	public List<Integer> getRoleUnitIds(Integer roleId, Integer enterpriseId) {
		 List<Integer> roleUnitIdList = new ArrayList<>();
		 if(roleId == 0 || roleId == 1) {
			 roleUnitIdList = netStructComponent.getUnitByEnterpriseId(enterpriseId);
			return roleUnitIdList;
		}   
		 if (roleId != null) {
	            if (redisUtil.get("roleUnit_" + roleId) == null) {
	            	QueryWrapper<RoleUnit> wrapper = new QueryWrapper<RoleUnit>();
	            	wrapper.lambda().eq(RoleUnit :: getRoleId, roleId);
	                List<RoleUnit> roleUnitList = this.list(wrapper);
	                for (RoleUnit roleUnit : roleUnitList) {
	                	roleUnitIdList.add(roleUnit.getUnitId());
	                }
	                redisUtil.set("roleUnit_" + roleId, StringIdsUtil.StringIds(roleUnitIdList));
	            }else {
	            	String roleUnitIds = (String) redisUtil.get("roleUnit_" + roleId);
	            	roleUnitIdList = StringIdsUtil.listIds(roleUnitIds);
	            }

	        }
	        return roleUnitIdList;
	}
}
