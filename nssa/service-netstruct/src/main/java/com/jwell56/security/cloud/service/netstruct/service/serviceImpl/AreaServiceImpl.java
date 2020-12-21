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
import com.jwell56.security.cloud.service.netstruct.mapper.AreaMapper;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.service.feign.RoleAreaComponent;
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
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

	@Autowired
	private RoleAreaComponent roleAreaComponent;
	
    @Override
    public Map<Integer, Area> getAreaMap() {
        Map<Integer, Area> areaMap = (Map<Integer, Area>) CommonCachePool.getData("area-id-map-cache");
        if (areaMap == null) {
            List<Area> areaList = this.list(null);
            Map<Integer, Area> areaMapTemp = new HashMap<>();
            areaList.forEach(area -> areaMapTemp.put(area.getAreaId(), area));
            CommonCachePool.setData("area-id-map-cache", areaMapTemp);
            areaMap = areaMapTemp;
        }
        return areaMap;
    }

    @Override
    public List<Area> listCache() {
        List<Area> areaList = (List<Area>) CommonCachePool.getData("areaListCache");
        if (areaList == null) {
            areaList = this.list(null);
            CommonCachePool.setData("areaListCache", areaList);
        }
        return areaList;
    }

    @Override
    public List<Integer> getChildIdList(Integer id) {
        List<Integer> idList = new ArrayList<>();
        if (id != null) {
            getChildrenId(id, idList);
        }
        return idList;
    }

    private void getChildrenId(Integer id, List<Integer> idList) {
        if (idList == null) {
            idList = new ArrayList<>();
        }
        idList.add(id);
        List<Area> allList = this.listCache();
        for (Area area : allList) {
            if (area.getPid().equals(id)) {
                getChildrenId(area.getAreaId(), idList);
            }
        }
    }

    @Override
    public List<TreeNode> treeList(Integer enterpriseId, List<Integer> roleList) {

        //TODO 临时处理null参数查询不到第一个数据的问题
//        List<AreaNav> areaNavList = areaNavMapper.selectList(null);
        QueryWrapper<Area> areaNavQueryWrapper = new QueryWrapper<>();
        areaNavQueryWrapper.lambda().eq(Area::getEnterpriseId, enterpriseId);
        areaNavQueryWrapper.lambda().ne(Area::getAreaId, 0);
        if(roleList != null) {
        	areaNavQueryWrapper.lambda().in(Area :: getAreaId, roleList);
        }
        List<Area> areaNavList = this.list(areaNavQueryWrapper);

        List<TreeNode> treeNodeList = new LinkedList<>();
        for (Area areaNav : areaNavList) {
            TreeNode treeNode = new TreeNode(areaNav.getAreaId(),
                    areaNav.getPid(),
                    areaNav.getName(),
                    null);
            treeNodeList.add(treeNode);
        }

        //重新组织成树状
        List<TreeNode> nodeList = TreeNodeUtils.getTreeList(treeNodeList, 0);

        return nodeList;
    }
    
    public List<Area> getAreaNavCache(Integer enterpriseId) {
        List<Area> areaNavList;
        CommonCachePool cachePool = new CommonCachePool();
        String cacheKey = "area-list-"+ enterpriseId;
        CommonDataCache dataCache = cachePool.get(cacheKey);
        if (dataCache == null) {
            //TODO 使用null查询查不到第一条，原因不明，暂时按照以下方式处理
            QueryWrapper<Area> areaNavQueryWrapper = new QueryWrapper<>();
            areaNavQueryWrapper.lambda().eq(Area::getEnterpriseId, enterpriseId);
            areaNavQueryWrapper.lambda().ne(Area::getAreaId, 0);
            areaNavList = this.list(areaNavQueryWrapper);
            dataCache = new CommonDataCache(cacheKey, areaNavList);
            cachePool.add(dataCache);
        } else {
            areaNavList = (List<Area>) dataCache.getData();
        }
        return areaNavList;
    }
    
    @Override
    public Integer getTopAreaById(Integer areaId) {
        Map<Integer, Area> areaMap = this.getAreaMap();
        Area area = areaMap.get(areaId);
        if (area != null) {
            if (area.getPid().equals(0)) {
                return area.getAreaId();
            } else {
                return getTopAreaById(area.getPid());
            }
        } else {
            return 0;
        }
    }
    
    @Override
    public List<Integer> getRoleTopAreaIdList(Integer roleId, Integer enterpriseId) {
        List<Integer> roleTopAreaIdList = new ArrayList<>();
        for (Integer areaId : roleAreaComponent.areaList(roleId,enterpriseId)) {
            Integer topId = this.getTopAreaById(areaId);
            if (!roleTopAreaIdList.contains(topId)) {
                roleTopAreaIdList.add(topId);
            }
        }
        return roleTopAreaIdList;
    }
    
    @Override
    public boolean areaExist(int pid, String areaName,Integer enterpriseId) {
        QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
        areaQueryWrapper.lambda().eq(Area::getPid, pid);
        areaQueryWrapper.lambda().eq(Area::getName, areaName);
        areaQueryWrapper.lambda().eq(Area :: getEnterpriseId, enterpriseId);
        List<Area> areaNavList = this.list(areaQueryWrapper);
        return areaNavList != null && areaNavList.size() > 0;
    }
    
}
