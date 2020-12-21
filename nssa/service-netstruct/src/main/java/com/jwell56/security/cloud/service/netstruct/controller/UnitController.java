package com.jwell56.security.cloud.service.netstruct.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AttributeImpl.UseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.TreeNode;
import com.jwell56.security.cloud.service.netstruct.entity.Unit;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.service.IUnitService;
import com.jwell56.security.cloud.service.netstruct.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.netstruct.utils.RedisUtil;
import com.jwell56.security.cloud.service.netstruct.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
@RestController
@RequestMapping("/unit")
public class UnitController {
    @Autowired
    private IUnitService iUnitService;
    
    @Autowired
    private RoleUnitComponent roleUnitComponent;
    
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("根据区域名称，模糊查询")
    @RequestMapping(value = "/searchByName", method = RequestMethod.GET)
    public ResultObject searchByName(String unitName) {
        try {
            QueryWrapper<Unit> unitQueryWrapper = new QueryWrapper<>();
            unitQueryWrapper.lambda().like(Unit::getName, unitName);
            return ResultObject.data(iUnitService.list(unitQueryWrapper));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("根据单位ID查名称")
    @RequestMapping(value = "/searchById", method = RequestMethod.GET)
    public ResultObject searchById(Integer unitId) {
        try {
        	Map<Integer, Unit> areaMap = iUnitService.getUnitMap();
        	Unit unit = areaMap.get(unitId);
//        	if(unit != null) redisUtil.set("unitName_" + unit.getUnitId(), unit.getName());
            return ResultObject.data(unit);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("根据区域ID查子单位")
    @RequestMapping(value = "/getChildren", method = RequestMethod.GET)
    public ResultObject getChildren(Integer unitId) {
        try {
            List<Integer> unitIdList = new ArrayList<>();
            if (unitId != null) {
                unitIdList.add(unitId);
                getUnitChildrenId(unitId, unitIdList);
            }
            return ResultObject.data(unitIdList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("根据区域ID查子单位")
    @RequestMapping(value = "/getChildrens", method = RequestMethod.GET)
    public ResultObject getChildrens(String unitIds) {
        try {
        	List<Integer> unitIdLists = new ArrayList<>();
        	List<Integer> unitList = StringIdsUtil.listIds(unitIds);
            if (unitList != null && !unitList.isEmpty()) {
            	for(Integer unitId : unitList) {
            		List<Integer> unitIdList = new ArrayList<>();
            		unitIdList.add(unitId);
            		getUnitChildrenId(unitId, unitIdList);
            		unitIdLists.addAll(unitIdList);
            	}
            }
            return ResultObject.data(unitIdLists);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    /**
     * 获取网络区域模块列表(设置)
     *
     * @return
     */
    @ApiOperation(value = "网络区域模块列表(设置)", notes = "获取网络区域模块列表")
    @RequestMapping(value = {"/treelist1"}, method = RequestMethod.GET)
    public ResultObject treelist1() {

        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            List<TreeNode> unitNavMapList = iUnitService.treeList(userInfo.getEnterpriseId(), null);
//            permission(roleUnitList, unitNavMapList);

            resultObject.setData(unitNavMapList);
            resultObject.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }
    
    /**
     * 获取网络区域模块列表
     *
     * @return
     */
    @ApiOperation(value = "网络区域模块列表", notes = "获取网络区域模块列表")
    @RequestMapping(value = {"/treelist"}, method = RequestMethod.GET)
    public ResultObject treelist() {

        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	List<Integer> roleUnitList = new ArrayList<>();
      	
      	if(userInfo.getRoleType() == 3) {
      		Set<Integer> set = new HashSet<Integer>();
      		List<Integer> roleIdUnitList = roleUnitComponent.unitList(userInfo.getRoleId(),userInfo.getEnterpriseId());
      		for(Integer unitId : roleIdUnitList) {
      			getParentId(unitId, set);
      		}
      		roleUnitList = new ArrayList<Integer>(set);
      	}else if(userInfo.getRoleType() == 2){
      		QueryWrapper<Unit> queryWrapper = new QueryWrapper<Unit>();
      		queryWrapper.lambda().eq(Unit :: getEnterpriseId, userInfo.getEnterpriseId());
      		List<Unit> units = iUnitService.list(queryWrapper);
      		for(Unit unit : units) {
      			roleUnitList.add(unit.getUnitId());
      		}
      	}else if(userInfo.getRoleType() == 1){
      		QueryWrapper<Unit> queryWrapper = new QueryWrapper<Unit>();
      		queryWrapper.lambda().eq(Unit :: getEnterpriseId, userInfo.getEnterpriseId());
      		List<Unit> units = iUnitService.list(queryWrapper);
      		for(Unit unit : units) {
      			roleUnitList.add(unit.getUnitId());
      		}
      	}
        try {
            List<TreeNode> unitNavMapList = iUnitService.treeList(userInfo.getEnterpriseId(), roleUnitList);
//            permission(roleUnitList, unitNavMapList);
//
//            Iterator<TreeNode> iterator = unitNavMapList.iterator();
//            while(iterator.hasNext()) {
//            	TreeNode node = iterator.next();
//            	if(node.getPermission().equals("0")) iterator.remove();
//            }
            resultObject.setData(unitNavMapList);
            resultObject.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }

    @ApiOperation("根据单位ID查父单位")
    @RequestMapping(value = "/getParent", method = RequestMethod.GET)
    public ResultObject getParent(Integer unitId) {
        try {
            List<Integer> unitIdList = new ArrayList<>();
            if (unitId != null) {
            	getParentId(unitId, unitIdList);
            }
            return ResultObject.data(unitIdList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("根据单位ID查询名称")
    @RequestMapping(value = "/getUnitName", method = RequestMethod.GET)
    public ResultObject getUnitName(Integer unitId) {
        try {
        	StringBuilder sb = new StringBuilder();
        	List<String> nameList = new ArrayList<>();
            if (unitId != null) {
            	getParentName(unitId, nameList);
            }
            Collections.reverse(nameList);
            for(String name : nameList) {
            	sb.append(name+"-");
            }
            String res = sb.substring(0, sb.length()-1);
            if(!StringUtils.isEmpty(res)) redisUtil.set("unitName_" + unitId, res);
            return ResultObject.data(res);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("根据单位名称查询单位ID")
    @RequestMapping(value = "/getUnitIdByName", method = RequestMethod.GET)
    public ResultObject getUnitIdByName(String unitName, Integer enterpriseId) {
        try {
        	List<String> namesList = Arrays.asList(unitName.split("-"));
        	
            return ResultObject.data(getUnitId(namesList, enterpriseId));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    /**
     * 单位查询列表
     *
     * @return
     */
    @ApiOperation(value = "单位查询列表", notes = "单位查询列表")
    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
            }
    )
    public ResultObject list() {

        ResultObject resultObject = new ResultObject();

        try {
            List<Map<String, Object>> unitMapList = iUnitService.listMaps(null);

            resultObject.setData(unitMapList);
            resultObject.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }

    /**
     * 一级网络单位
     *
     * @return
     */
    @ApiOperation(value = "一级网络单位", notes = "查询一级网络单位")
    @RequestMapping(value = {"/oneLevel"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)

    })
    public ResultObject getOneLevel() {

        ResultObject resultObject = new ResultObject();
        
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	
        QueryWrapper<Unit> unitQueryWrapper = new QueryWrapper<>();
        unitQueryWrapper.select("unit_id", "pid", "name");

        unitQueryWrapper.lambda().eq(Unit::getPid, 0);
        List<Unit> unitList = iUnitService.list(unitQueryWrapper);

        //权限限制
        if (userInfo.getRoleType() != 1) {//admin角色不做限制
            List<Unit> roleUnitList = new ArrayList<>();
            List<Integer> roleUnitIdList = iUnitService.getRoleTopUnitIdList(userInfo.getRoleId(), userInfo.getEnterpriseId());
            for (Unit tempUnit : unitList) {
                if (roleUnitIdList.contains(tempUnit.getUnitId())) {
                    roleUnitList.add(tempUnit);
                }
            }
            unitList = roleUnitList;
        }

        Unit unit = new Unit();
        unit.setName("全部");
        unitList.add(unit);

        final List<String> regulationOrder = Lists.newArrayList("全部");
        Collections.sort(unitList, new Comparator<Unit>() {
            public int compare(Unit o1, Unit o2) {
                //按照高、中、低顺序
                int io1 = regulationOrder.indexOf(o1.getName());
                int io2 = regulationOrder.indexOf(o2.getName());
                return io2 - io1;
            }
        });
        resultObject.setData(unitList);

        resultObject.setCode(HttpServletResponse.SC_OK);
        return resultObject;
    }

    /**
     * 根据一级ID获取二级
     *
     * @return
     */
    @ApiOperation(value = "根据一级ID获取二级", notes = "根据一级ID获取二级")
    @RequestMapping(value = {"/twoLevel"}, method = RequestMethod.GET)
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(paramType = "query", name = "id", value = "一级ID", dataType = "Integer"),
                    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
            }
    )
    public ResultObject getTwoLevel(Integer id) {

        ResultObject resultObject = new ResultObject();

        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            resultObject.setCode(HttpServletResponse.SC_OK);
            QueryWrapper<Unit> unitQueryWrapper = new QueryWrapper<>();
            unitQueryWrapper.select("unit_id", "pid", "name");

            unitQueryWrapper.lambda().eq(Unit::getPid, id);
            List<Unit> unitList = iUnitService.list(unitQueryWrapper);

            //权限限制
            if (userInfo.getRoleType() != 1) {//admin角色不做限制
                List<Unit> roleUnitList = new ArrayList<>();
                List<Integer> roleUnitIdList = iUnitService.getRoleTopUnitIdList(userInfo.getRoleId(),userInfo.getEnterpriseId());
                for (Unit tempUnit : unitList) {
                    if (roleUnitIdList.contains(tempUnit.getUnitId())) {
                        roleUnitList.add(tempUnit);
                    }
                }
                unitList = roleUnitList;
            }

            Unit unit = new Unit();
            unit.setName("全部");
            unitList.add(unit);

            final List<String> regulationOrder = Lists.newArrayList("全部");
            Collections.sort(unitList, new Comparator<Unit>() {
                public int compare(Unit o1, Unit o2) {
                    //按照高、中、低顺序
                    int io1 = regulationOrder.indexOf(o1.getName());
                    int io2 = regulationOrder.indexOf(o2.getName());
                    return io2 - io1;
                }
            });

            resultObject.setData(unitList);

        } catch (Exception e) {
            e.printStackTrace();
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;

    }
    
    @ApiOperation("获取该企业下的所有区域")
    @RequestMapping(value = "/getUnitByEnterpriseId", method = RequestMethod.GET)
    public ResultObject getUnitByEnterpriseId(Integer enterpriseId) {
        try {
        	List<Integer> roleUnitList = new ArrayList<Integer>();
            QueryWrapper<Unit> unitQueryWrapper = new QueryWrapper<>();
            unitQueryWrapper.lambda().eq(Unit::getEnterpriseId, enterpriseId);
            List<Unit> units = iUnitService.list(unitQueryWrapper);
      		for(Unit unit : units) {
      			roleUnitList.add(unit.getUnitId());
      		}
            return ResultObject.data(roleUnitList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    private void permission(List<Integer> roleUnitList, List<TreeNode> treeNodeList) {
        for (TreeNode treeNode : treeNodeList) {
        	permission(roleUnitList, treeNode.getChildren());
            boolean flag = false;
            for (Integer unitId : roleUnitList) {
                if (unitId.equals(treeNode.getId())) {
                    flag = true;
                    break;
                }
            }
            treeNode.setPermission(flag ? "1" : "0");
        }
    }
    
    private void getUnitChildrenId(Integer unitId, List<Integer> unitIdList) {
        QueryWrapper<Unit> areaQueryWrapper = new QueryWrapper<>();
        areaQueryWrapper.lambda().eq(Unit::getPid, unitId);
        List<Unit> unitList = iUnitService.list(areaQueryWrapper);
        for (Unit unit : unitList) {
            unitIdList.add(unit.getUnitId());
            getUnitChildrenId(unit.getUnitId(), unitIdList);
        }
    }
    
    private void getParentId(Integer unitId, List<Integer> unitIdList){
    	Unit unit = iUnitService.getById(unitId);
    	unitIdList.add(unitId);
    	if(unit.getPid() == 0 || unit.getPid() == null) {
    		return;
    	}
    	getParentId(unit.getPid(), unitIdList);
    }
    
    private void getParentId(Integer unitId, Set<Integer> areaIdSet){
    	String cacheKey = "unit_by_id"+ unitId;
    	Unit unit = (Unit) CommonCachePool.getData(cacheKey);
    	if(unit == null) {
    		unit = iUnitService.getById(unitId);
    	}
    	areaIdSet.add(unitId);
    	if(unit.getPid() == 0 || unit.getPid() == null) {
    		return;
    	}
    	getParentId(unit.getPid(), areaIdSet);
    }
    
    private void getParentName(Integer unitId, List<String> nameList){
    	Unit unit = iUnitService.getById(unitId);
    	nameList.add(unit.getName());
    	if(unit.getPid() == 0 || unit.getPid() == null) {
    		return;
    	}
    	getParentName(unit.getPid(), nameList);
    }
    
    private Integer getUnitId(List<String> namesList, Integer enterpriseId) {
    	QueryWrapper<Unit> queryWrapper = new QueryWrapper<Unit>();
		queryWrapper.lambda().eq(Unit :: getEnterpriseId, enterpriseId);
		queryWrapper.lambda().eq(Unit :: getName, namesList.get(namesList.size() -1));
		List<Unit> units = iUnitService.list(queryWrapper);
		return unitIdByName(namesList, units);
    }
    
    private Integer unitIdByName(List<String> namesList, List<Unit> units) {
    	if(units.isEmpty() || units == null) return 0;
    	if(units.size() == 1) return units.get(0).getUnitId();
    	List<String> reList = new ArrayList<String>();
    	for(Unit unit : units) {
    		getParentName(unit.getUnitId(), reList);
    		Collections.reverse(reList);
    		if(reList.stream().sorted().collect(Collectors.joining()).equals(namesList.stream().sorted().collect(Collectors.joining()))) {
    			return unit.getUnitId();
    		}
    		reList.clear();
    	}
    	return 0;
    }
}
