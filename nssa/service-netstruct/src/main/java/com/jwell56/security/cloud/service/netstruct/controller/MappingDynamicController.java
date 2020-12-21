package com.jwell56.security.cloud.service.netstruct.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.common.IPUtil;
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.MappingDynamic;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.entity.commons.NetStructParam;
import com.jwell56.security.cloud.service.netstruct.entity.commons.PageParam;
import com.jwell56.security.cloud.service.netstruct.entity.vo.MappingDynamicVo;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.service.IMappingDynamicService;
import com.jwell56.security.cloud.service.netstruct.service.feign.AssetComponent;
import com.jwell56.security.cloud.service.netstruct.service.feign.UserComponent;
import com.jwell56.security.cloud.service.netstruct.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "动态映射相关的接口",tags = {"原始日志相关的controller"})
@RequestMapping("/mapping/dynamic/")
public class MappingDynamicController {
	
	@Autowired
	private IMappingDynamicService iMappingDynamicService;
	
    @Autowired
    private IAreaService iAreaService;
    
    @Autowired
    private AssetComponent assetComponent;
    
    @Autowired
    private UserComponent userComponent;

	@ApiOperation("添加动态IP映射")
	@PostMapping("/add")
	public ResultObject add(@RequestBody MappingDynamic mappingDynamic) {
		ResultObject res = new ResultObject<>();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		mappingDynamic.setUserId(userInfo.getUserId());
//		if(userInfo.getRoleType() == 1) {
//			mappingDynamic.setEnterpriseId(userInfo.getEnterpriseFlag());
//		}else {
//			mappingDynamic.setEnterpriseId(userInfo.getEnterpriseId());
//		}
		mappingDynamic.setEnterpriseId(userInfo.getEnterpriseId());
		boolean b = iMappingDynamicService.save(mappingDynamic);
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增动态IP映射成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增动态IP映射失败");
        }		
		return res;
	}
	
    @ApiOperation("删除动态IP映射")
	@DeleteMapping("delete")
	public ResultObject delete(String mappingDynamicIds) {
		ResultObject res = new ResultObject();
		
		boolean b = iMappingDynamicService.removeByIds(StringIdsUtil.listIds(mappingDynamicIds));
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除动态IP映射成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除动态IP映射失败");
        }
		
		return res;
	}
    
    @ApiOperation("修改动态IP映射")
	@PostMapping("update")
	public ResultObject update(@RequestBody MappingDynamic mappingDynamic) {
		ResultObject res = new ResultObject();
		
		boolean b = iMappingDynamicService.updateById(mappingDynamic);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("修改动态IP映射成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改动态IP映射失败");
        }
		return res;
	}
    
    @ApiOperation("动态IP映射详情页面")
	@GetMapping("detail")
	public ResultObject detail(Integer mappingDynamicId) {
		ResultObject res = new ResultObject();
		MappingDynamic mappingDynamic = iMappingDynamicService.getById(mappingDynamicId);
		res.setData(mappingDynamic);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
    
    @ApiOperation("动态IP映射页面")
    @GetMapping("paging")
    public ResultObject paging(PageParam pageParam,
                               NetStructParam netStructParam,
                               String sourceIP, String mapIP, String keyWord) {
    	
    	ResultObject res = new ResultObject<>();
    	QueryWrapper<MappingDynamic> queryWrapper = new QueryWrapper<MappingDynamic>();
    	
    	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
//		if(userInfo.getRoleType() == 1) {
//			queryWrapper.lambda().eq(MappingDynamic :: getEnterpriseId, userInfo.getEnterpriseFlag());
//		}else {
//		}
		queryWrapper.lambda().eq(MappingDynamic :: getEnterpriseId, userInfo.getEnterpriseId());
    	if(!StringUtils.isEmpty(sourceIP)) {
    		queryWrapper.le("inet_aton(source_start_ip)", IPUtil.ipToLong(sourceIP));
    		queryWrapper.ge("inet_aton(source_end_ip)", IPUtil.ipToLong(sourceIP));
//    		queryWrapper.lambda().like(MappingDynamic :: getSourceStartIp, sIp).or().like(MappingDynamic :: getSourceEndIp, sIp);
		}
    	if(!StringUtils.isEmpty(mapIP)) {
    		queryWrapper.le("inet_aton(mapping_start_ip)", IPUtil.ipToLong(mapIP));
    		queryWrapper.ge("inet_aton(mapping_end_ip)", IPUtil.ipToLong(mapIP));
//    		queryWrapper.lambda().like(MappingDynamic :: getMappingEndIp, dIp).or().like(MappingDynamic :: getMappingStartIp, dIp);
		}
    	if(!StringUtils.isEmpty(keyWord)) {
    		queryWrapper.lambda().like(MappingDynamic :: getSourceStartIp, keyWord).or().like(MappingDynamic :: getMappingEndIp, keyWord).or()
    		.like(MappingDynamic :: getSourceEndIp, keyWord).or().like(MappingDynamic :: getMappingStartIp, keyWord);
		}
    	
    	if(!netStructParam.sAreaIdList().isEmpty()) {
    		queryWrapper.lambda().in(MappingDynamic :: getSourceAreaId, netStructParam.sAreaIdList());
		}
		
		if(!netStructParam.sUnitIdList().isEmpty()) {
			queryWrapper.lambda().in(MappingDynamic :: getSourceUnitId, netStructParam.sUnitIdList());
		}
		
		if(!netStructParam.dAreaIdList().isEmpty()) {
    		queryWrapper.lambda().in(MappingDynamic :: getMappingAreaId, netStructParam.dAreaIdList());
		}
		
		if(!netStructParam.dUnitIdList().isEmpty()) {
			queryWrapper.lambda().in(MappingDynamic :: getMappingUnitId, netStructParam.dUnitIdList());
		}
    	
    	queryWrapper.lambda().orderByDesc(MappingDynamic :: getCreateTime);
		IPage<MappingDynamic> mappingDynamics = iMappingDynamicService.page(pageParam.iPage(), queryWrapper);
		List<MappingDynamicVo> mappingDynamicList = new ArrayList();
		IPage<MappingDynamicVo> mappingDynamicPage = new Page<MappingDynamicVo>();
		BeanUtils.copyProperties(mappingDynamics, mappingDynamicPage);
		for(MappingDynamic mappingDynamic : mappingDynamics.getRecords()) {
			MappingDynamicVo vo = new MappingDynamicVo();
			BeanUtils.copyProperties(mappingDynamic, vo);
			Map<Integer, Area> areaMap = iAreaService.getAreaMap();
	        Area area = areaMap.get(mappingDynamic.getSourceAreaId());
			vo.setSourceAreaName(area.getName());
			area = areaMap.get(mappingDynamic.getMappingAreaId());
			vo.setMappingAreaName(area.getName());
			vo.setDeviceName(assetComponent.getAssetById(mappingDynamic.getDeviceId()));
			vo.setUserName(userComponent.getUserName(mappingDynamic.getUserId()));
			mappingDynamicList.add(vo);
		}
		mappingDynamicPage.setRecords(mappingDynamicList);
		res.setData(mappingDynamicPage);
		res.setCode(HttpServletResponse.SC_OK);
    	return res;
    }
}
