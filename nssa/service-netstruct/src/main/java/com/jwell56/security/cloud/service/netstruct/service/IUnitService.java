package com.jwell56.security.cloud.service.netstruct.service;

import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.TreeNode;
import com.jwell56.security.cloud.service.netstruct.entity.Unit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
public interface IUnitService extends IService<Unit> {
    Map<Integer, Unit> getUnitMap();

    List<Unit> listCache();

    List<Integer> getChildIdList(Integer id);
    
    List<TreeNode> treeList(Integer enterpriseId, List<Integer> roleList);
    
    Integer getTopUnitById(Integer unitId);
    
    List<Integer> getRoleTopUnitIdList(Integer roleId,Integer enterpriseId);
    
    boolean unitExist(int pid, String unitName,Integer enterpriseId);
    
    
}
