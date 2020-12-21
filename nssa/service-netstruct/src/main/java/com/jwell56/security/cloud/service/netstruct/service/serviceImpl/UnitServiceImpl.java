package com.jwell56.security.cloud.service.netstruct.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.common.cache.CommonDataCache;
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.TreeNode;
import com.jwell56.security.cloud.service.netstruct.entity.Unit;
import com.jwell56.security.cloud.service.netstruct.mapper.UnitMapper;
import com.jwell56.security.cloud.service.netstruct.service.IUnitService;
import com.jwell56.security.cloud.service.netstruct.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.netstruct.utils.TreeNodeUtils;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements IUnitService {

	@Autowired
	private RoleUnitComponent roleUnitComponent;
	
    @Override
    public Map<Integer, Unit> getUnitMap() {
        Map<Integer, Unit> unitMap = (Map<Integer, Unit>) CommonCachePool.getData("unit-id-map-cache");
        if (unitMap == null) {
            List<Unit> unitList = this.list(null);
            Map<Integer, Unit> unitMapTemp = new HashMap<>();
            unitList.forEach(unit -> unitMapTemp.put(unit.getUnitId(), unit));
            CommonCachePool.setData("unit-id-map-cache", unitMapTemp);
            unitMap = unitMapTemp;
        }
        return unitMap;
    }

    @Override
    public List<Unit> listCache() {
        List<Unit> unitList = (List<Unit>) CommonCachePool.getData("unitListCache");
        if (unitList == null) {
            unitList = this.list(null);
            CommonCachePool.setData("unitListCache", unitList);
        }
        return unitList;
    }

    @Override
    public List<Integer> getChildIdList(Integer id) {
        List<Integer> idList = new ArrayList<>();
        if (id != null) {
            getChildrenId(id, idList);
        }
        return idList;
    }
    
    @Override
    public List<TreeNode> treeList(Integer enterpriseId, List<Integer> roleList) {
        //TODO 临时处理null参数查询不到第一个数据的问题
//        List<UnitNav> unitNavList = unitNavMapper.selectList(null);
    	QueryWrapper<Unit> unitNavQueryWrapper = new QueryWrapper<>();
        unitNavQueryWrapper.lambda().ne(Unit::getUnitId, 0);
        unitNavQueryWrapper.lambda().eq(Unit::getEnterpriseId, enterpriseId);
        if(roleList != null) {
        	unitNavQueryWrapper.lambda().in(Unit :: getUnitId, roleList);
        }
        List<Unit> unitNavList = this.list(unitNavQueryWrapper);
        List<TreeNode> treeNodeList = new LinkedList<>();
        for (Unit unitNav : unitNavList) {
            TreeNode treeNode = new TreeNode(unitNav.getUnitId(),
                    unitNav.getPid(),
                    unitNav.getName(),
                    null);
            treeNodeList.add(treeNode);
        }

        //重新组织成树状
        List<TreeNode> nodeList = TreeNodeUtils.getTreeList(treeNodeList, 0);

        return nodeList;
    }

    public List<Unit> getUnitCache(Integer enterpriseId) {
        List<Unit> unitNavList;
        CommonCachePool cachePool = new CommonCachePool();
        String cacheKey = "unit-list-" + enterpriseId;
        CommonDataCache dataCache = cachePool.get(cacheKey);
        if (dataCache == null) {
            //TODO 使用null查询查不到第一条，原因不明，暂时按照以下方式处理
            QueryWrapper<Unit> unitNavQueryWrapper = new QueryWrapper<>();
            unitNavQueryWrapper.lambda().ne(Unit::getUnitId, 0);
            unitNavQueryWrapper.lambda().eq(Unit::getEnterpriseId, enterpriseId);
            unitNavList = this.list(unitNavQueryWrapper);
            dataCache = new CommonDataCache(cacheKey, unitNavList);
            cachePool.add(dataCache);
        } else {
            unitNavList = (List<Unit>) dataCache.getData();
        }
        return unitNavList;
    }

    private void getChildrenId(Integer id, List<Integer> idList) {
        if (idList == null) {
            idList = new ArrayList<>();
        }
        idList.add(id);
        List<Unit> allList = this.listCache();
        for (Unit unit : allList) {
            if (unit.getPid().equals(id)) {
                getChildrenId(unit.getUnitId(), idList);
            }
        }
    }
    
    @Override
    public Integer getTopUnitById(Integer unitId) {
        Map<Integer, Unit> unitMap = this.getUnitMap();
        Unit unit = unitMap.get(unitId);
        if (unit != null) {
            if (unit.getPid().equals(0)) {
                return unit.getUnitId();
            } else {
                return getTopUnitById(unit.getPid());
            }
        } else {
            return 0;
        }
    }
    
    @Override
    public List<Integer> getRoleTopUnitIdList(Integer roleId,Integer enterpriseId) {
        List<Integer> roleTopUnitIdList = new ArrayList<>();
        for (Integer areaId : roleUnitComponent.unitList(roleId,enterpriseId)) {
            Integer topId = this.getTopUnitById(areaId);
            if (!roleTopUnitIdList.contains(topId)) {
                roleTopUnitIdList.add(topId);
            }
        }
        return roleTopUnitIdList;
    }
    
    @Override
    public boolean unitExist(int pid, String unitName,Integer enterpriseId) {
        QueryWrapper<Unit> unitQueryWrapper = new QueryWrapper<>();
        unitQueryWrapper.lambda().eq(Unit::getPid, pid);
        unitQueryWrapper.lambda().eq(Unit::getName, unitName);
        unitQueryWrapper.lambda().eq(Unit :: getEnterpriseId, enterpriseId);
        List<Unit> unitList = this.list(unitQueryWrapper);
        return unitList != null && unitList.size() > 0;
    }
}
