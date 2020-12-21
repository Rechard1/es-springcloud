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
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.service.feign.RoleAreaComponent;
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
@RequestMapping("/area")
public class AreaController {
    @Autowired
    private IAreaService iAreaService;
    
    @Autowired
    private RoleAreaComponent roleAreaComponent;
    
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("根据区域名称，模糊查询")
    @RequestMapping(value = "/searchByName", method = RequestMethod.GET)
    public ResultObject searchByName(String areaName) {
        try {
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.lambda().like(Area::getName, areaName);
            return ResultObject.data(iAreaService.list(areaQueryWrapper));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("根据区域ID查名称")
    @RequestMapping(value = "/searchById", method = RequestMethod.GET)
    public ResultObject searchById(Integer areaId) {
        try {
            Map<Integer, Area> areaMap = iAreaService.getAreaMap();
            Area area = areaMap.get(areaId);
//            if(area != null) redisUtil.set("areaName_" + area.getAreaId(), area.getName());
            return ResultObject.data(area);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("根据区域ID查子区域")
    @RequestMapping(value = "/getChildren", method = RequestMethod.GET)
    public ResultObject getChildren(Integer areaId) {
        try {
            List<Integer> areaIdList = new ArrayList<>();
            if (areaId != null) {
                areaIdList.add(areaId);
                getAreaChildrenId(areaId, areaIdList);
            }
            return ResultObject.data(areaIdList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("根据区域ID查子区域")
    @RequestMapping(value = "/getChildrens", method = RequestMethod.GET)
    public ResultObject getChildrens(String areaIds) {
        try {
        	List<Integer> areaIdLists = new ArrayList<>();
        	List<Integer> areaList = StringIdsUtil.listIds(areaIds);
            if (areaList != null && !areaList.isEmpty()) {
            	for(Integer areaId : areaList) {
            		List<Integer> areaIdList = new ArrayList<>();
            		areaIdList.add(areaId);
            		getAreaChildrenId(areaId, areaIdList);
            		areaIdLists.addAll(areaIdList);
            	}
            }
            return ResultObject.data(areaIdLists);
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
            List<TreeNode> areaNavMapList = iAreaService.treeList(userInfo.getEnterpriseId(),null);
//            permission(roleAreaList, areaNavMapList);
            resultObject.setData(areaNavMapList);
            resultObject.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }
    
    @ApiOperation(value = "网络区域模块列表", notes = "获取网络区域模块列表")
    @RequestMapping(value = {"/treelist"}, method = RequestMethod.GET)
    public ResultObject treelist() {

        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	List<Integer> roleAreaList = new ArrayList<Integer>();
      	if(userInfo.getRoleType() == 3) {
      		Set<Integer> set = new HashSet<Integer>();
      		List<Integer> roleIdAreaList = roleAreaComponent.areaList(userInfo.getRoleId(),userInfo.getEnterpriseId());
      		for(Integer areaId : roleIdAreaList) {
      			getParentId(areaId, set);
      		}
      		roleAreaList = new ArrayList<Integer>(set);
      	}else if(userInfo.getRoleType() == 2){
      		QueryWrapper<Area> queryWrapper = new QueryWrapper<Area>();
      		queryWrapper.lambda().eq(Area :: getEnterpriseId, userInfo.getEnterpriseId());
      		List<Area> areas = iAreaService.list(queryWrapper);
      		for(Area area : areas) {
      			roleAreaList.add(area.getAreaId());
      		}
      	}else if(userInfo.getRoleType() == 1){
      		QueryWrapper<Area> queryWrapper = new QueryWrapper<Area>();
      		queryWrapper.lambda().eq(Area :: getEnterpriseId, userInfo.getEnterpriseId());
      		List<Area> areas = iAreaService.list(queryWrapper);
      		for(Area area : areas) {
      			roleAreaList.add(area.getAreaId());
      		}
      	}
        try {
            List<TreeNode> areaNavMapList = iAreaService.treeList(userInfo.getEnterpriseId(),roleAreaList);
//            permission(roleAreaList, areaNavMapList);
//
//            Iterator<TreeNode> iterator = areaNavMapList.iterator();
//            while(iterator.hasNext()) {
//            	TreeNode node = iterator.next();
//            	if(node.getPermission().equals("0")) iterator.remove();
//            }
            resultObject.setData(areaNavMapList);
            resultObject.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }
    
    @ApiOperation("根据区域ID查父区域")
    @RequestMapping(value = "/getParent", method = RequestMethod.GET)
    public ResultObject getParent(Integer areaId) {
        try {
            List<Integer> areaIdList = new ArrayList<>();
            if (areaId != null) {
            	getParentId(areaId, areaIdList);
            }
            return ResultObject.data(areaIdList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("根据区域ID查询名称")
    @RequestMapping(value = "/getAreaName", method = RequestMethod.GET)
    public ResultObject getAreaName(Integer areaId) {
        try {
        	StringBuilder sb = new StringBuilder();
        	List<String> nameList = new ArrayList<>();
            if (areaId != null) {
            	getParentName(areaId, nameList);
            }
            Collections.reverse(nameList);
            for(String name : nameList) {
            	sb.append(name+"-");
            }
            String res = sb.substring(0, sb.length()-1);
            if(!StringUtils.isEmpty(res)) redisUtil.set("areaName_" + areaId, res);
            return ResultObject.data(res);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("根据区域名称查询区域ID")
    @RequestMapping(value = "/getAreaIdByName", method = RequestMethod.GET)
    public ResultObject getAreaIdByName(String areaName, Integer enterpriseId) {
        try {
        	List<String> namesList = Arrays.asList(areaName.split("-"));
        	
            return ResultObject.data(getAreaId(namesList, enterpriseId));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    /**
     * 区域查询列表
     *
     * @return
     */
    @ApiOperation(value = "区域查询列表", notes = "区域查询列表")
    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
            }
    )
    public ResultObject list() {

        ResultObject resultObject = new ResultObject();

        try {
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.select("area_id", "pid", "name");
            List<Area> areaList = iAreaService.list(areaQueryWrapper);

            resultObject.setData(areaList);

            resultObject.setCode(HttpServletResponse.SC_OK);
            return resultObject;

        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }


    /**
     * 一级网络区域
     *
     * @return
     */
    @ApiOperation(value = "一级网络区域", notes = "查询一级网络区域")
    @RequestMapping(value = {"/oneLevel"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)

    })
    public ResultObject getOneLevel() {

        ResultObject resultObject = new ResultObject();
        
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	
        QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
        areaQueryWrapper.select("area_id", "pid", "name");

        areaQueryWrapper.lambda().eq(Area::getPid, 0);
        List<Area> areaList = iAreaService.list(areaQueryWrapper);

        //权限限制
        if (userInfo.getRoleType() != 1) {//admin角色不做限制
            List<Area> roleAreaList = new ArrayList<>();
            List<Integer> roleAreaIdList = iAreaService.getRoleTopAreaIdList(userInfo.getRoleId(), userInfo.getEnterpriseId());
            for (Area tempArea : areaList) {
                if (roleAreaIdList.contains(tempArea.getAreaId())) {
                    roleAreaList.add(tempArea);
                }
            }
            areaList = roleAreaList;
        }
        Area area = new Area();
        area.setName("全部");
        areaList.add(area);

        final List<String> regulationOrder = Lists.newArrayList("全部");
        Collections.sort(areaList, new Comparator<Area>() {
            public int compare(Area o1, Area o2) {
                //按照高、中、低顺序
                int io1 = regulationOrder.indexOf(o1.getName());
                int io2 = regulationOrder.indexOf(o2.getName());
                return io2 - io1;
            }
        });
        resultObject.setData(areaList);

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
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.select("area_id", "pid", "name");

            areaQueryWrapper.lambda().eq(Area::getPid, id);
            List<Area> areaList = iAreaService.list(areaQueryWrapper);

            //权限限制
            if (userInfo.getRoleType() != 1) {//admin角色不做限制
                List<Area> roleAreaList = new ArrayList<>();
                List<Integer> roleAreaIdList = iAreaService.getRoleTopAreaIdList(userInfo.getRoleId(), userInfo.getEnterpriseId());
                for (Area tempArea : areaList) {
                    if (roleAreaIdList.contains(tempArea.getAreaId())) {
                        roleAreaList.add(tempArea);
                    }
                }
                areaList = roleAreaList;
            }

            Area area = new Area();
            area.setName("全部");
            areaList.add(area);

            final List<String> regulationOrder = Lists.newArrayList("全部");
            Collections.sort(areaList, new Comparator<Area>() {
                public int compare(Area o1, Area o2) {
                    //按照高、中、低顺序
                    int io1 = regulationOrder.indexOf(o1.getName());
                    int io2 = regulationOrder.indexOf(o2.getName());
                    return io2 - io1;
                }
            });

            resultObject.setData(areaList);

        } catch (Exception e) {
            e.printStackTrace();
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;

    }
    
    @ApiOperation("获取该企业下的所有区域")
    @RequestMapping(value = "/getAreaByEnterpriseId", method = RequestMethod.GET)
    public ResultObject getAreaByEnterpriseId(Integer enterpriseId) {
        try {
        	List<Integer> roleAreaList = new ArrayList<Integer>();
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.lambda().eq(Area::getEnterpriseId, enterpriseId);
            List<Area> areas = iAreaService.list(areaQueryWrapper);
      		for(Area area : areas) {
      			roleAreaList.add(area.getAreaId());
      		}
            return ResultObject.data(roleAreaList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    private void permission(List<Integer> roleAreaList, List<TreeNode> treeNodeList) {
        for (TreeNode treeNode : treeNodeList) {
        	permission(roleAreaList, treeNode.getChildren());
            boolean flag = false;
            for (Integer areaId : roleAreaList) {
                if (areaId.equals(treeNode.getId())) {
                    flag = true;
                    break;
                }
            }
            treeNode.setPermission(flag ? "1" : "0");
        }
    }
    private void getAreaChildrenId(Integer areaId, List<Integer> areaIdList) {
        QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
        areaQueryWrapper.lambda().eq(Area::getPid, areaId);
        List<Area> areaList = iAreaService.list(areaQueryWrapper);
        for (Area area : areaList) {
            areaIdList.add(area.getAreaId());
            getAreaChildrenId(area.getAreaId(), areaIdList);
        }
    }
    
    private void getParentId(Integer areaId, List<Integer> areaIdList){
    	Area area = iAreaService.getById(areaId);
    	areaIdList.add(areaId);
    	if(area.getPid() == 0 || area.getPid() == null) {
    		return;
    	}
    	getParentId(area.getPid(), areaIdList);
    }
    
    private void getParentId(Integer areaId, Set<Integer> areaIdSet){
    	String cacheKey = "area_by_id"+ areaId;
    	Area area = (Area) CommonCachePool.getData(cacheKey);
    	if(area == null) {
    		area = iAreaService.getById(areaId);
    	}
    	areaIdSet.add(areaId);
    	if(area.getPid() == 0 || area.getPid() == null) {
    		return;
    	}
    	getParentId(area.getPid(), areaIdSet);
    }
    
    private void getParentName(Integer areaId, List<String> nameList){
    	Area area = iAreaService.getById(areaId);
    	nameList.add(area.getName());
    	if(area.getPid() == 0 || area.getPid() == null) {
    		return;
    	}
    	getParentName(area.getPid(), nameList);
    }
    
    private Integer getAreaId(List<String> namesList, Integer enterpriseId) {
    	QueryWrapper<Area> queryWrapper = new QueryWrapper<Area>();
		queryWrapper.lambda().eq(Area :: getEnterpriseId, enterpriseId);
		queryWrapper.lambda().eq(Area :: getName, namesList.get(namesList.size() -1));
		List<Area> areas = iAreaService.list(queryWrapper);
		return areaIdByName(namesList, areas);
    }
    
    private Integer areaIdByName(List<String> namesList, List<Area> areas) {
    	if(areas.isEmpty() || areas == null) return 0;
    	if(areas.size() == 1) return areas.get(0).getAreaId();
    	List<String> reList = new ArrayList<String>();
    	for(Area area : areas) {
    		getParentName(area.getAreaId(), reList);
    		Collections.reverse(reList);
    		if(reList.stream().sorted().collect(Collectors.joining()).equals(namesList.stream().sorted().collect(Collectors.joining()))) {
    			return area.getAreaId();
    		}
    		reList.clear();
    	}
    	return 0;
    }
}
