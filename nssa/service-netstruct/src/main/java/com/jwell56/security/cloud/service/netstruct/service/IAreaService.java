package com.jwell56.security.cloud.service.netstruct.service;

import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.TreeNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
public interface IAreaService extends IService<Area> {
    Map<Integer, Area> getAreaMap();

    List<Area> listCache();

    List<Integer> getChildIdList(Integer id);
    
    List<TreeNode> treeList(Integer enterpriseId, List<Integer> roleList);
    
    Integer getTopAreaById(Integer areaId);
    
    List<Integer> getRoleTopAreaIdList(Integer roleId,Integer enterpriseId);
    
    boolean areaExist(int pid, String areaName,Integer enterpriseId);
}
