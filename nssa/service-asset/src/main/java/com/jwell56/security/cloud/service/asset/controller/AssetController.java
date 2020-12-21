package com.jwell56.security.cloud.service.asset.controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.IPUtil;
import com.jwell56.security.cloud.service.asset.entity.Asset;
import com.jwell56.security.cloud.service.asset.entity.AssetFind;
import com.jwell56.security.cloud.service.asset.entity.AssetFindApplication;
import com.jwell56.security.cloud.service.asset.entity.SysDevice;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.entity.commons.NetStructParam;
import com.jwell56.security.cloud.service.asset.entity.commons.PageParam;
import com.jwell56.security.cloud.service.asset.entity.excel.AssetExcel;
import com.jwell56.security.cloud.service.asset.entity.vo.AssetNameVO;
import com.jwell56.security.cloud.service.asset.entity.vo.AssetPage;
import com.jwell56.security.cloud.service.asset.service.IAssetFindApplicationService;
import com.jwell56.security.cloud.service.asset.service.IAssetFindService;
import com.jwell56.security.cloud.service.asset.service.IAssetService;
import com.jwell56.security.cloud.service.asset.service.ISysDeviceService;
import com.jwell56.security.cloud.service.asset.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.asset.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.asset.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.asset.utils.ExcelUtils;
import com.jwell56.security.cloud.service.asset.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(value = "资产接口",tags = {"资产相关的controller"})
@RestController
@RequestMapping("/asset")
public class AssetController {

	@Autowired
	private IAssetService assetService;
	
    @Autowired
    private NetStructComponent netStructComponent;

//	@Autowired
//	private RoleAreaComponent roleAreaComponent;
//
//	@Autowired
//	private RoleUnitComponent roleUnitComponent;
	
    @Autowired
    private ISysDeviceService iSysDeviceService;
    
    @Autowired
    private IAssetFindService iAssetFindService;
    
    @Autowired
    private IAssetFindApplicationService iAssetFindApplicationService;

	@ApiOperation("根据资产名称，模糊查询")
	@RequestMapping(value = "/searchByName", method = RequestMethod.GET)
	public ResultObject searchByName(String assetName) {
		try {
			QueryWrapper<Asset> assetQueryWrapper = new QueryWrapper<>();
			assetQueryWrapper.lambda().like(Asset::getName, assetName);
			return ResultObject.data(assetService.list(assetQueryWrapper));
		} catch (Exception e) {
			return ResultObject.exception(e);
		}
	}
	
	@ApiOperation("根据资产id,查询资产")
	@GetMapping("getAssetById")
	public ResultObject getAssetById(Integer assetId) {
		return ResultObject.data(assetService.getById(assetId));
	}

	@ApiOperation("根据资产id,查询资产名称")
	@GetMapping("getAssetNameById")
	public ResultObject getAssetNameById(Integer assetId) {
		Asset asset = assetService.getById(assetId);

		AssetNameVO assetNameVO = new AssetNameVO();
		BeanUtils.copyProperties(asset,assetNameVO);
		assetNameVO.setAreaName(netStructComponent.getAreaName(asset.getAreaId()));
		assetNameVO.setUnitName(netStructComponent.getUnitName(asset.getUnitId()));
		return ResultObject.data(assetNameVO);
	}
	
	@GetMapping("getAssetByIp")
	public ResultObject getAssetByIp(String ip) {
		QueryWrapper<Asset> wrapper = new QueryWrapper<Asset>();
		wrapper.lambda().eq(Asset :: getIp, ip);
		Asset asset = assetService.getOne(wrapper);
		return ResultObject.data(asset);
	}
	
	@ApiOperation("新增资产")
	@PostMapping("add")
	@Transactional
	public ResultObject add(@RequestBody Asset asset) {
		ResultObject res = new ResultObject<>();
		if(!asset.getIp().isEmpty() && asset.getIp() != null) {
        	if(!IPUtil.ipCheck(asset.getIp())) {
        		res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setSuccess(Boolean.FALSE);
        		res.setMsg("请填写正确ip");
    			return res;
        	}
        }
		Asset valAsset = new Asset();
		valAsset.setAreaId(asset.getAreaId());
		valAsset.setUnitId(asset.getUnitId());
		QueryWrapper<Asset> wrapper = new QueryWrapper<Asset>();
		valAsset.setIp(asset.getIp());
		wrapper.setEntity(valAsset);
		valAsset = assetService.getOne(wrapper);
		if(valAsset != null) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
    		res.setMsg("同区域单位下的设备ip相同，请重新填入ip！");
			return res;
		}
		
		valAsset = new Asset();
		valAsset.setAreaId(asset.getAreaId());
		valAsset.setUnitId(asset.getUnitId());
		valAsset.setName(asset.getName());
		wrapper.setEntity(valAsset);
		valAsset = assetService.getOne(wrapper);
		
		if(valAsset != null) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
    		res.setMsg("同区域单位下的设备名称相同，请重新填入设备名称！");
			return res;
		}
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		asset.setUserId(userInfo.getUserId());
		asset.setEnterpriseId(userInfo.getEnterpriseId());
//		if(userInfo.getRoleType() == 1) {
//			asset.setEnterpriseId(userInfo.getEnterpriseFlag());
//		}else {
//			asset.setEnterpriseId(userInfo.getEnterpriseId());
//		}
		
		boolean b = assetService.save(asset);
        if (b) {
        	if (asset.getAssetFindId()!= null && asset.getAssetFindId() != 0) {
                AssetFind assetFind = new AssetFind();
                BeanUtils.copyProperties(asset, assetFind);
                b = iAssetFindService.saveOrUpdate(assetFind);
            }
        	
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增资产成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增资产失败");
        }
		return res;
	}
	
	@ApiOperation("删除资产设备")
	@DeleteMapping("delete")
	public ResultObject deleteDevice(String assetIds) {
		ResultObject res = new ResultObject();
		
		boolean b = assetService.removeByIds(StringIdsUtil.listIds(assetIds));
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除资产成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除资产失败");
        }
		
		return res;
	}
	
	@ApiOperation("修改资产设备")
	@PostMapping("update")
	public ResultObject update(@RequestBody Asset asset) {
		ResultObject res = new ResultObject();
		
		if(!asset.getIp().isEmpty() && asset.getIp() != null) {
        	if(!IPUtil.ipCheck(asset.getIp())) {
        		res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setSuccess(Boolean.FALSE);
        		res.setMsg("请填写正确ip");
    			return res;
        	}
        }
		Asset valAsset = new Asset();
		valAsset.setAreaId(asset.getAreaId());
		valAsset.setUnitId(asset.getUnitId());
		QueryWrapper<Asset> wrapper = new QueryWrapper<Asset>();
		valAsset.setIp(asset.getIp());
		wrapper.setEntity(valAsset);
		valAsset = assetService.getOne(wrapper);
		if(valAsset != null && !valAsset.getAssetId().equals(asset.getAssetId())) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
    		res.setMsg("同区域单位下的设备ip相同，请重新填入ip！");
			return res;
		}
		
		valAsset = new Asset();
		valAsset.setAreaId(asset.getAreaId());
		valAsset.setUnitId(asset.getUnitId());
		valAsset.setName(asset.getName());
		wrapper.setEntity(valAsset);
		valAsset = assetService.getOne(wrapper);
		
		if(valAsset != null && !valAsset.getAssetId().equals(asset.getAssetId())) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
    		res.setMsg("同区域单位下的设备名称相同，请重新填入设备名称！");
			return res;
		}
		
		boolean b = assetService.updateById(asset);
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
	
	@ApiOperation("资产详情页面")
	@GetMapping("detail")
	public ResultObject detail(Integer assetId) {
		ResultObject res = new ResultObject();
		Map<String, Object> resMap = new HashMap(8);
		AssetPage resAsset = assetService.detail(assetId);
		if(resAsset.getAssetFindId() != null && resAsset.getAssetFindId() != 0) {
			QueryWrapper<AssetFindApplication> wrapper = new QueryWrapper<AssetFindApplication>();
			wrapper.lambda().eq(AssetFindApplication :: getAssetFindId, resAsset.getAssetFindId());
			List<AssetFindApplication> applications = iAssetFindApplicationService.list(wrapper);
			resMap.put("application", applications);
		}else {
			resMap.put("application", null);
		}
		resMap.put("assetDetail", resAsset);
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
	
	@ApiOperation("资产页面")
	@GetMapping("paging")
	public ResultObject paging(PageParam pageParam,
                               NetStructParam netStructParam,
                               String name, boolean important,
                               String ip, String type, String expandInfo, String keyword) {
		ResultObject res = new ResultObject();
        QueryWrapper<Asset> assetQueryWrapper = new QueryWrapper<Asset>();
        //权限处理
        assetQueryWrapper = assetService.queryWrapperForAreaUnit(netStructParam.areaIdList(), netStructParam.unitIdList(), assetQueryWrapper);
        if(!StringUtils.isEmpty(name)) {
			assetQueryWrapper.lambda().like(Asset :: getName, name);
		}
		if(!StringUtils.isEmpty(ip)) {
			String[] ips = ip.split("-");
            if(ips.length == 1){
            	assetQueryWrapper.between("inet_aton(ip)", IPUtil.ipToLong(ips[0])+"", IPUtil.ipToLong(ips[0])+"");
            }
            if(ips.length == 2){
            	//between范围不包括后面一个参数，所以加一
            	assetQueryWrapper.between("inet_aton(ip)", IPUtil.ipToLong(ips[0])+"", IPUtil.ipToLong(ips[1])+"");
            }
		}
		if(!StringUtils.isEmpty(type)) {
			assetQueryWrapper.lambda().like(Asset :: getType, type);
		}
		if(!StringUtils.isEmpty(expandInfo)) {
			assetQueryWrapper.lambda().like(Asset :: getExpandInfo, expandInfo);
		}
		if(!StringUtils.isEmpty(keyword)) {
			assetQueryWrapper.lambda().eq(Asset :: getIp, keyword).or().
			eq(Asset :: getType, keyword).or().
			like(Asset :: getName, keyword);
			
		}
		if(important == true) {
			assetQueryWrapper.lambda().eq(Asset :: getImportant, 1);
		}
		assetQueryWrapper.lambda().orderByDesc(Asset :: getCreateTime);
		IPage<Asset> assets = assetService.page(pageParam.iPage(), assetQueryWrapper);
		List<AssetPage> assetList = new ArrayList();
		IPage<AssetPage> assetPage = new Page<AssetPage>();
		BeanUtils.copyProperties(assets, assetPage);
		for(Asset asset : assets.getRecords()) {
			AssetPage page = new AssetPage();
			BeanUtils.copyProperties(asset, page);
			page.setAreaName(netStructComponent.getAreaName(asset.getAreaId()));
			page.setUnitName(netStructComponent.getUnitName(asset.getUnitId()));
			page.setImportantType(asset.getImportant() == 1 ? "是" : "否");
			assetList.add(page);
		}
		assetPage.setRecords(assetList);
		res.setData(assetPage);
		res.setCode(HttpServletResponse.SC_OK);
        return res;
	}
	
	@ApiOperation("资产导出")
	@GetMapping("exprotExcel")
	public ResponseEntity exprotExcel(NetStructParam netStructParam,
                                      String name, boolean important,
                                      String ip, String type, String expandInfo,
                                      String assetIds) {
		List<Asset> resList = new ArrayList<>();
		QueryWrapper<Asset> assetQueryWrapper = new QueryWrapper<Asset>();
		
		if(StringIdsUtil.listIds(assetIds) != null && !StringIdsUtil.listIds(assetIds).isEmpty()) {
			resList = (List<Asset>) assetService.listByIds(StringIdsUtil.listIds(assetIds));
		}else {
			 //权限处理
	        assetQueryWrapper = assetService.queryWrapperForAreaUnit(netStructParam.areaIdList(), netStructParam.unitIdList(), assetQueryWrapper);
			if(!StringUtils.isEmpty(name)) {
				assetQueryWrapper.lambda().like(Asset :: getName, name);
			}
			if(!StringUtils.isEmpty(ip)) {
				String[] ips = ip.split("-");
	            if(ips.length == 1){
	            	assetQueryWrapper.between("inet_aton(ip)", IPUtil.ipToLong(ips[0])+"", IPUtil.ipToLong(ips[0])+"");
	            }
	            if(ips.length == 2){
	            	//between范围不包括后面一个参数，所以加一
	            	assetQueryWrapper.between("inet_aton(ip)", IPUtil.ipToLong(ips[0])+"", IPUtil.ipToLong(ips[1])+"");
	            }
			}
			if(!StringUtils.isEmpty(type)) {
				assetQueryWrapper.lambda().like(Asset :: getType, type);
			}
			if(!StringUtils.isEmpty(expandInfo)) {
				assetQueryWrapper.lambda().like(Asset :: getExpandInfo, expandInfo);
			}
			if(important == true) {
				assetQueryWrapper.lambda().eq(Asset :: getImportant, 1);
			}
			assetQueryWrapper.lambda().orderByDesc(Asset :: getCreateTime);
			resList = assetService.list(assetQueryWrapper);
		}
		
		List<AssetExcel> dataList = new ArrayList<>();
		for(Asset asset : resList) {
			AssetExcel excel = new AssetExcel();
			BeanUtils.copyProperties(asset, excel);
			excel.setAreaName(netStructComponent.getAreaName(asset.getAreaId()));
			excel.setUnitName(netStructComponent.getUnitName(asset.getUnitId()));
			excel.setImportantType(asset.getImportant() == 1 ? "是" : "否");
			dataList.add(excel);
		}
//		resList.stream().forEach(x -> {
//			AssetExcel excel = new AssetExcel();
//			BeanUtils.copyProperties(x, excel);
//			excel.setAreaName(netStructComponent.getAreaName(x.getAreaId()));
//			excel.setUnitName(netStructComponent.getUnitName(x.getUnitId()));
//			excel.setImportantType(x.getImportant() == 1 ? "是" : "否");
//			dataList.add(excel);
//		});
		//数据写入到字节流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelUtils.writeExcel(bos, AssetExcel.class, dataList);

        //下载文件
        String fileName = "Asset.xls";
        log.info("开始下载导出的Excel文件");
        return ExcelUtils.downloadExcel(fileName, bos);
	}
	
	@ApiOperation("修改资产设备")
	@GetMapping("important")
	public ResultObject important(String assetIds) {
		ResultObject res = new ResultObject();
		//list<Integer>
		boolean b = assetService.important(StringIdsUtil.listIds(assetIds));
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
	
	@ApiOperation("资产导入")
	@PostMapping("importExcel")
	public ResultObject importExcel(@RequestBody MultipartFile excel) {
		ResultObject res = new ResultObject();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		
		List<AssetPage> assetPageList = new ArrayList<>();
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			List<Object> assetExcel = ExcelUtils.readExcel(excel, new AssetExcel());
			for(Object o : assetExcel) {
				AssetPage assetPage = new AssetPage();
				BeanUtils.copyProperties(o, assetPage);
				assetPageList.add(assetPage);
			}
			BeanUtils.copyProperties(assetExcel, assetPageList);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		if(assetPageList.isEmpty()) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("请导入正确Excel,导入失败");
            return res;
            }
		for(AssetPage assetPage : assetPageList) {
			if(StringUtils.isEmpty(assetPage.getAreaName()) || StringUtils.isEmpty(assetPage.getUnitName())) {
				res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            res.setSuccess(Boolean.FALSE);
	            res.setMsg("请导入正确Excel,导入失败");
	            return res;
			}
			Asset asset = new Asset();
			BeanUtils.copyProperties(assetPage, asset);
			asset.setAreaId(netStructComponent.getAreaId(assetPage.getAreaName(), userInfo.getEnterpriseId()));
			asset.setUnitId(netStructComponent.getUnitId(assetPage.getUnitName(), userInfo.getEnterpriseId()));
			asset.setImportant(assetPage.getImportantType().equals("是") ? 1 : 0);
			asset.setUserId(userInfo.getUserId());
			asset.setEnterpriseId(userInfo.getEnterpriseId());
			assetList.add(asset);
		}
		boolean b = assetService.saveBatch(assetList);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("导入成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("导入失败");
        }
		return res;
	}

	@ApiOperation("设备")
	@GetMapping("all")
	public ResultObject all() {
		ResultObject res = new ResultObject();
		QueryWrapper<Asset> assetQueryWrapper = new QueryWrapper<Asset>();
		//权限处理
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();

		//权限控制
//		List<Integer> roleAreaIdList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
//		List<Integer> roleUnitIdList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
		//当无权限时,返回，不进行查询
//		if (roleAreaIdList == null || roleAreaIdList.isEmpty() || roleUnitIdList == null || roleUnitIdList.isEmpty()) {
//			assetQueryWrapper.apply("1<>1");
//		} else {
//			assetQueryWrapper.lambda().in(Asset::getAreaId, roleAreaIdList);
//			assetQueryWrapper.lambda().in(Asset::getUnitId, roleUnitIdList);
//		}
		List<Asset> asset = assetService.list(assetQueryWrapper);
		res.setData(asset);
		res.setCode(HttpServletResponse.SC_OK);
		return res;
	}
	
	@ApiOperation("设备列表")
	@GetMapping("list")
	public ResultObject lsit() {
		ResultObject res = new ResultObject();
		QueryWrapper<Asset> assetQueryWrapper = new QueryWrapper<Asset>();
		//权限处理
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		assetQueryWrapper.lambda().in(Asset :: getEnterpriseId, userInfo.getEnterpriseId());
		List<Asset> asset = assetService.list(assetQueryWrapper);
		res.setData(asset);
		res.setCode(HttpServletResponse.SC_OK);
		return res;
	}
	@GetMapping("/device-type")
	public ResultObject getIpListByDeviceType(
			@RequestParam("deviceType") String deviceType
	){
		ResultObject resultObject = new ResultObject();

		System.err.println("123");

		List<String> resultList = assetService.getIpListByDeviceType(deviceType);

		resultObject.setData(resultList);
		resultObject.setSuccess(Boolean.TRUE);
		resultObject.setCode(HttpServletResponse.SC_OK);
		return resultObject;
	}

	@GetMapping("/ip")
	public ResultObject getAssetStrByIp(
			@RequestParam("ip") String ip
	){
		ResultObject resultObject = new ResultObject();
		Asset asset = assetService.getAssetByIp(ip);
		String assetStr = JSONObject.toJSONString(asset);

		resultObject.setData(assetStr);
		resultObject.setCode(HttpServletResponse.SC_OK);
		return resultObject;

	}
	
	@GetMapping("isDelete")
	public ResultObject isDelete(Integer areaId, Integer unitId) {
		ResultObject res = new ResultObject<>();
		Map<String, Object> resMap = new HashMap();
		StringBuilder sb = new StringBuilder();
		QueryWrapper<Asset> wrapper = new QueryWrapper<Asset>();
		wrapper.lambda().eq(Asset :: getUnitId, unitId).or().eq(Asset :: getAreaId, areaId);
		QueryWrapper<SysDevice> wrapper1 = new QueryWrapper<SysDevice>();
		wrapper1.lambda().eq(SysDevice :: getUnitId, unitId).or().eq(SysDevice :: getAreaId, areaId);
		List<Asset> assets = assetService.list(wrapper);
		List<SysDevice> devices = iSysDeviceService.list(wrapper1);
		for(Asset asset : assets) {
			sb.append(asset.getName());
			sb.append(",");
		}
		for(SysDevice device : devices) {
			sb.append(device.getName());
			sb.append(",");
		}
		if(assets.isEmpty() && devices.isEmpty()) {
			resMap.put("isDelete", true);
			resMap.put("msg", null);
		}else {
			sb.append("上述设备的区域或者单位已被占用，请先删除上述设备后，再删除该网络结构。");
			resMap.put("isDelete", false);
			resMap.put("msg", sb.toString());
		}
		res.setData(resMap);
		return res;
	}
	
	@GetMapping("getAssetId")
	public Integer getAssetId(String ip, Integer areaId, Integer unitId) {
		QueryWrapper<Asset> wrapper = new QueryWrapper<Asset>();
		wrapper.lambda().eq(Asset :: getIp, ip);
		wrapper.lambda().eq(Asset :: getAreaId, areaId);
		wrapper.lambda().eq(Asset :: getUnitId, unitId);
		Asset asset = assetService.getOne(wrapper);
		if(asset == null) {
			return 0;
		}
		return asset.getAssetId();
	}
}
