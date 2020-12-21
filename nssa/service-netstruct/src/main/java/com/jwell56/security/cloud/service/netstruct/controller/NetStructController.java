package com.jwell56.security.cloud.service.netstruct.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.common.IPUtil;
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.Intranet;
import com.jwell56.security.cloud.service.netstruct.entity.NetStruct;
import com.jwell56.security.cloud.service.netstruct.entity.NetStructEx;
import com.jwell56.security.cloud.service.netstruct.entity.ParamBody;
import com.jwell56.security.cloud.service.netstruct.entity.Unit;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.entity.commons.NetStructParam;
import com.jwell56.security.cloud.service.netstruct.entity.commons.PageParam;
import com.jwell56.security.cloud.service.netstruct.entity.dto.NetStructDto;
import com.jwell56.security.cloud.service.netstruct.entity.dto.SearchIpsDto;
import com.jwell56.security.cloud.service.netstruct.entity.dto.SearchSqlDto;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.service.IAssetService;
import com.jwell56.security.cloud.service.netstruct.service.IIntranetService;
import com.jwell56.security.cloud.service.netstruct.service.INetStructService;
import com.jwell56.security.cloud.service.netstruct.service.IUnitService;
import com.jwell56.security.cloud.service.netstruct.service.feign.AssetComponent;
import com.jwell56.security.cloud.service.netstruct.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

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
@RequestMapping("/netStruct")
public class NetStructController {

    @Autowired
    IAreaService iAreaService;

    @Autowired
    IUnitService iUnitService;

    @Autowired
    INetStructService iNetStructService;

    @Autowired
    IIntranetService iIntranetService;

    @Autowired
    IAssetService iAssetService;
    
    @Autowired
    private AssetComponent assetComponent;

    @ApiOperation("获取区域、单位")
    @RequestMapping(value = "/getName", method = RequestMethod.GET)
    public ResultObject getName(String ip, Integer areaId, Integer unitId) {
        try {
            NetStructDto netStructDto = iNetStructService.getNetStructDto(ip, areaId, unitId);

            Map<String, Object> map = new HashMap<>();
            List<String> areaNameList = new ArrayList<>();
            if (netStructDto != null && netStructDto.getAreaId() != null && netStructDto.getAreaId() != 0) {
                areaNameList = getAreaNameList(netStructDto.getAreaId(), null);
            }
            List<String> unitNameList = new ArrayList<>();
            if (netStructDto != null && netStructDto.getUnitId() != null && netStructDto.getUnitId() != 0) {
                unitNameList = getUnitNameList(netStructDto.getUnitId(), null);
            }
            map.put("areaNameList", areaNameList);
            map.put("unitNameList", unitNameList);

            return ResultObject.data(map);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    private List<String> getAreaNameList(Integer id, List<String> areaNameList) {
        if (areaNameList == null) {
            areaNameList = new ArrayList<>();
        }
        Area area = iAreaService.getById(id);
        if (area != null && area.getPid() != null && area.getPid() != 0) {
            areaNameList = getAreaNameList(area.getPid(), areaNameList);
        }
        areaNameList.add(area == null ? "" : area.getName());
        return areaNameList;
    }

    private List<String> getUnitNameList(Integer id, List<String> unitNameList) {
        if (unitNameList == null) {
            unitNameList = new ArrayList<>();
        }
        Unit unit = iUnitService.getById(id);
        if (unit != null && unit.getPid() != null && unit.getPid() != 0) {
            unitNameList = getUnitNameList(unit.getPid(), unitNameList);
        }
        unitNameList.add(unit == null ? "" : unit.getName());
        return unitNameList;
    }

    @ApiOperation("获取区域、单位id")
    @RequestMapping(value = "/getIdByIp", method = RequestMethod.POST)
    public ResultObject getIdByIp(@RequestBody ParamBody paramBody) {
        try {
            long startTime = System.currentTimeMillis();

            Integer resultAreaId = 0;
            Integer resultUnitId = 0;
            List<Integer> areaIdList = StringIdsUtil.listIds(paramBody.getAreaIds());
            List<Integer> unitIdList = StringIdsUtil.listIds(paramBody.getUnitIds());

//            //优先从资产判断区域单位
//            List<Asset> assetList = iAssetService.listCache();
//            Asset resultAsset = null;
//            for (Asset asset : assetList) {
//                if ((areaIdList.isEmpty() || areaIdList.contains(asset.getAreaId())) &&
//                        (unitIdList.isEmpty() || unitIdList.contains(asset.getUnitId())) &&
//                        ip.equals(asset.getIp())) {
//                    resultAsset = asset;
//                }
//            }
//
//            if (resultAsset != null) {
//                resultAreaId = resultAsset.getAreaId();
//                resultUnitId = resultAsset.getUnitId();
//            }

            //其次从区域单位网络结构判断
            if (resultAreaId == 0 && resultUnitId == 0) {
                List<NetStructEx> netStructList = iNetStructService.listExCache(paramBody.getEnterpriseId());

                List<NetStructEx> netStructSearchList = new ArrayList<>();
                for (NetStructEx netStruct : netStructList) {
                    if (areaIdList.contains(netStruct.getAreaId()) && unitIdList.contains(netStruct.getUnitId())) {
                        Long startIp = netStruct.getSIpNum();
                        Long endIp = netStruct.getDIpNum();
                        Long ipn = IPUtil.ipToLong(paramBody.getIp());
                        if (ipn >= startIp && ipn <= endIp) {
                            NetStructEx netStructSearch = new NetStructEx();
                            BeanUtils.copyProperties(netStruct, netStructSearch);
                            netStructSearchList.add(netStructSearch);
                        }
                    }
                }

                //取ip段范围最小的值
                Map<Long, NetStruct> resultMap = new HashMap<>();
                netStructSearchList.forEach(netStruct ->
                        resultMap.put(netStruct.getDIpNum() - netStruct.getSIpNum(), netStruct));

                NetStruct netStructResult = resultMap.isEmpty() ? null : resultMap.get(Collections.min(resultMap.keySet()));

                if (netStructResult != null) {
                    resultAreaId = netStructResult.getAreaId();
                    resultUnitId = netStructResult.getUnitId();
                } else {
                    Long ipn = IPUtil.ipToLong(paramBody.getIp());
                    List<Intranet> intranetList = iIntranetService.listCache();
                    boolean flag = false;
                    for (Intranet intranet : intranetList) {
                        flag = (intranet.startIpNum() <= ipn && intranet.endIpNum() >= ipn) || flag;
                    }
                    if (!flag) {
                        resultAreaId = 0;
                        resultUnitId = 0;
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("areaId", resultAreaId);
            map.put("unitId", resultUnitId);


            long endTime = System.currentTimeMillis();
            float excTime = (float) (endTime - startTime) / 1000;
            System.out.println(";耗时：" + excTime + "秒==============");

            return ResultObject.data(map);
        } catch (
                Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("根据区域单位查询的sql")
    @RequestMapping(value = "/getSql", method = RequestMethod.POST)
    public ResultObject getSql(@RequestBody SearchSqlDto searchSqlDto) {
        try {
            String sql = "";

            QueryWrapper<NetStruct> netStructQueryWrapper = new QueryWrapper<>();
            if (searchSqlDto.getAreaIdList() != null && !searchSqlDto.getAreaIdList().isEmpty()) {
                netStructQueryWrapper.lambda().in(NetStruct::getAreaId, searchSqlDto.getAreaIdList());
            }
            if (searchSqlDto.getUnitIdList() != null && !searchSqlDto.getUnitIdList().isEmpty()) {
                netStructQueryWrapper.lambda().in(NetStruct::getUnitId, searchSqlDto.getUnitIdList());
            }
            List<NetStruct> netStructList = iNetStructService.list(netStructQueryWrapper);
            if (netStructList != null && !netStructList.isEmpty()) {
                for (NetStruct netStruct : netStructList) {
                    sql += sql.equals("") ? "" : " or ";
                    if (searchSqlDto.getIsNum()) {
                        sql += searchSqlDto.getField() + " between " + IPUtil.ipToLong(netStruct.getStartIp()) + " and " + IPUtil.ipToLong(netStruct.getEndIp()) + " ";
                    } else {
                        sql += "INET_ATON(" + searchSqlDto.getField() + ")" + " between " + IPUtil.ipToLong(netStruct.getStartIp()) + " and " + IPUtil.ipToLong(netStruct.getEndIp()) + " ";
                    }
                }
            }

            return ResultObject.data(sql);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }


    @ApiOperation("根据区域单位查询对应ip段")
    @RequestMapping(value = "/getIps", method = RequestMethod.POST)
    public ResultObject getIps(@RequestBody SearchIpsDto searchIpsDto) {
        try {
        	List<Integer> areaIdList = StringIdsUtil.listIds(searchIpsDto.getAreaIds());
            List<Integer> unitIdList = StringIdsUtil.listIds(searchIpsDto.getUnitIds());
            List<NetStruct> netStructList = new ArrayList<>();
            QueryWrapper<NetStruct> netStructQueryWrapper = new QueryWrapper<>();
            if (areaIdList != null && !areaIdList.isEmpty()) {
                netStructQueryWrapper.lambda().in(NetStruct::getAreaId, areaIdList);
            }
            if (unitIdList != null && !unitIdList.isEmpty()) {
                netStructQueryWrapper.lambda().in(NetStruct::getUnitId, unitIdList);
            }
            netStructList = iNetStructService.list(netStructQueryWrapper);
            return ResultObject.data(netStructList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("添加IP段")
    @PostMapping("add")
    public ResultObject add(@RequestBody NetStruct netstruct) {
    	ResultObject res = new ResultObject<>();
    	//获取用户信息
    	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
    	netstruct.setUserId(userInfo.getUserId());
    	if(userInfo.getRoleType() == 1) {
    		netstruct.setEnterpriseId(userInfo.getEnterpriseFlag());
		}else {
			netstruct.setEnterpriseId(userInfo.getEnterpriseId());
		}
    	boolean b = iNetStructService.save(netstruct);
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增IP段成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增IP段失败");
        }
    	return res;
    }
    
    @ApiOperation("删除IP段设备")
	@DeleteMapping("delete")
	public ResultObject delete(Integer netstructId) {
		ResultObject res = new ResultObject();
		NetStruct net = iNetStructService.getById(netstructId);
		Map<String, Object> resMap = assetComponent.isDelete(net.getAreaId(), net.getUnitId());
		if(!(boolean) resMap.get("isDelete")) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
			res.setMsg((String) resMap.get("msg"));
			return res;
		}
		boolean b = iNetStructService.removeById(netstructId);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除IP段成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除IP段失败");
        }
		return res;
	}
    
    @ApiOperation("修改IP段设备")
	@PostMapping("update")
	public ResultObject update(@RequestBody NetStruct netstruct) {
		ResultObject res = new ResultObject();
		
		boolean b = iNetStructService.updateById(netstruct);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("修改资产成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改资产失败");
        }
		return res;
	}
    
    @ApiOperation("IP段页面")
	@GetMapping("paging")
	public ResultObject paging(PageParam pageParam, NetStructParam netStructParam,
                               String ip, String keyWord) {
		ResultObject res = new ResultObject();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<NetStruct> netStructQueryWrapper = new QueryWrapper<NetStruct>();
		if(!StringUtils.isEmpty(ip)) {
			String[] ips = ip.split("-");
            if(ips.length == 1){
//            	netStructQueryWrapper.between("inet_aton(ip)", IPUtil.ipToLong(ips[0])+"", IPUtil.ipToLong(ips[0])+"");
            	netStructQueryWrapper.le("inet_aton(start_ip)", IPUtil.ipToLong(ips[0])+"");
            	netStructQueryWrapper.ge("inet_aton(end_ip)", IPUtil.ipToLong(ips[0])+"");
            }
            if(ips.length == 2){
            	//between范围不包括后面一个参数，所以加一
//            	netStructQueryWrapper.between("inet_aton(ip)", IPUtil.ipToLong(ips[0])+"", IPUtil.ipToLong(ips[1])+"");
            	netStructQueryWrapper.le("inet_aton(start_ip)", IPUtil.ipToLong(ips[0])+"");
            	netStructQueryWrapper.ge("inet_aton(end_ip)", IPUtil.ipToLong(ips[1])+"");
            }
//			netStructQueryWrapper.lambda().like(NetStruct :: getStartIp, ip).or().like(NetStruct :: getEndIp, ip);
		}
		if(!netStructParam.areaIdList().isEmpty()) {
			netStructQueryWrapper.lambda().in(NetStruct :: getAreaId, netStructParam.areaIdList());
		}
		
		if(!netStructParam.unitIdList().isEmpty()) {
			netStructQueryWrapper.lambda().in(NetStruct :: getUnitId, netStructParam.unitIdList());
		}
		
		if(!StringUtils.isEmpty(keyWord)) {
			netStructQueryWrapper.lambda().like(NetStruct :: getStartIp, keyWord).or().like(NetStruct :: getEndIp, keyWord);
		}
		netStructQueryWrapper.lambda().eq(NetStruct :: getEnterpriseId, userInfo.getEnterpriseId());
		netStructQueryWrapper.lambda().orderByDesc(NetStruct :: getCreateTime);
//		netStructQueryWrapper.last("limit 0, 20");
		IPage<NetStruct> netStructs = iNetStructService.page(pageParam.iPage(), netStructQueryWrapper);
		IPage<NetStructDto> netStructPage = new Page<NetStructDto>();
		BeanUtils.copyProperties(netStructs, netStructPage);
		List<NetStructDto> NetStructDtos = new ArrayList<NetStructDto>();
		for(NetStruct netStruct : netStructs.getRecords()) {
			NetStructDto page = new NetStructDto();
			BeanUtils.copyProperties(netStruct, page);
			Map<Integer, Area> areaMap = iAreaService.getAreaMap();
	        Area area = areaMap.get(netStruct.getAreaId());
			page.setAreaName(area.getName());
			Map<Integer, Unit> unitMap = iUnitService.getUnitMap();
			Unit unit = unitMap.get(netStruct.getUnitId());
			page.setUnitName(unit.getName());
			NetStructDtos.add(page);
		}
		netStructPage.setRecords(NetStructDtos);
		res.setData(netStructPage);
		res.setCode(HttpServletResponse.SC_OK);
        return res;
	}
}
