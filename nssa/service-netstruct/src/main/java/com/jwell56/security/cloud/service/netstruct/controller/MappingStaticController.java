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
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.MappingDynamic;
import com.jwell56.security.cloud.service.netstruct.entity.MappingStatic;
import com.jwell56.security.cloud.service.netstruct.entity.Unit;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.entity.commons.NetStructParam;
import com.jwell56.security.cloud.service.netstruct.entity.commons.PageParam;
import com.jwell56.security.cloud.service.netstruct.entity.vo.MappingStaticVo;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.service.IMappingStaticService;
import com.jwell56.security.cloud.service.netstruct.service.IUnitService;
import com.jwell56.security.cloud.service.netstruct.service.feign.AssetComponent;
import com.jwell56.security.cloud.service.netstruct.service.feign.UserComponent;
import com.jwell56.security.cloud.service.netstruct.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/mapping/static")
public class MappingStaticController {

	@Autowired
	private IMappingStaticService iMappingStaticService;
	
    @Autowired
    private IAreaService iAreaService;
    
    @Autowired
    private UserComponent userComponent;
    
    @Autowired
    private IUnitService iUnitService;

	@ApiOperation("添加静态IP映射")
	@PostMapping("/add")
	public ResultObject add(@RequestBody MappingStatic mappingStatic) {
		ResultObject res = new ResultObject<>();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		mappingStatic.setUserId(userInfo.getUserId());
		if(userInfo.getRoleType() == 1) {
			mappingStatic.setEnterpriseId(userInfo.getEnterpriseFlag());
		}else {
			mappingStatic.setEnterpriseId(userInfo.getEnterpriseId());
		}
		boolean b = iMappingStaticService.save(mappingStatic);
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增静态IP映射成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增静态IP映射失败");
        }		
		return res;
	}
	
    @ApiOperation("删除静态IP映射")
	@DeleteMapping("delete")
	public ResultObject delete(String mappingStaticIds) {
		ResultObject res = new ResultObject();
		
		boolean b = iMappingStaticService.removeByIds(StringIdsUtil.listIds(mappingStaticIds));
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除静态IP映射成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除静态IP映射失败");
        }
		
		return res;
	}
    
    @ApiOperation("修改静态IP映射")
	@PostMapping("update")
	public ResultObject update(@RequestBody MappingStatic mappingStatic) {
		ResultObject res = new ResultObject();
		
		boolean b = iMappingStaticService.updateById(mappingStatic);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("修改静态IP映射成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改静态IP映射失败");
        }
		return res;
	}
    
    @ApiOperation("静态IP映射详情页面")
	@GetMapping("detail")
	public ResultObject detail(Integer mappingStaticId) {
		ResultObject res = new ResultObject();
		MappingStatic mappingStatic = iMappingStaticService.getById(mappingStaticId);
		res.setData(mappingStatic);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
    
    @ApiOperation("静态IP映射页面")
    @GetMapping("paging")
    public ResultObject paging(PageParam pageParam,
                               NetStructParam netStructParam,
                               String sourceIP, String mapIP, String keyWord) {
    	
    	ResultObject res = new ResultObject<>();
    	QueryWrapper<MappingStatic> queryWrapper = new QueryWrapper<MappingStatic>();
    	
    	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		if(userInfo.getRoleType() == 1) {
			queryWrapper.lambda().eq(MappingStatic :: getEnterpriseId, userInfo.getEnterpriseFlag());
		}else {
			queryWrapper.lambda().eq(MappingStatic :: getEnterpriseId, userInfo.getEnterpriseId());
		}
    	if(!StringUtils.isEmpty(sourceIP)) {
    		queryWrapper.lambda().like(MappingStatic :: getSourceIp, sourceIP);
		}
    	if(!StringUtils.isEmpty(mapIP)) {
    		queryWrapper.lambda().like(MappingStatic :: getMappingIp, mapIP);
		}
    	if(!StringUtils.isEmpty(keyWord)) {
    		queryWrapper.lambda().like(MappingStatic :: getSourceIp, keyWord).or().like(MappingStatic :: getMappingIp, keyWord);
		}
    	
    	if(!netStructParam.sAreaIdList().isEmpty()) {
    		queryWrapper.lambda().in(MappingStatic :: getSourceAreaId, netStructParam.sAreaIdList());
		}
		
		if(!netStructParam.sUnitIdList().isEmpty()) {
			queryWrapper.lambda().in(MappingStatic :: getSourceUnitId, netStructParam.sUnitIdList());
		}
		
		if(!netStructParam.dAreaIdList().isEmpty()) {
    		queryWrapper.lambda().in(MappingStatic :: getMappingAreaId, netStructParam.dAreaIdList());
		}
		
		if(!netStructParam.dUnitIdList().isEmpty()) {
			queryWrapper.lambda().in(MappingStatic :: getMappingUnitId, netStructParam.dUnitIdList());
		}
		
    	queryWrapper.lambda().orderByDesc(MappingStatic :: getCreateTime);
		IPage<MappingStatic> mappingStatics = iMappingStaticService.page(pageParam.iPage(), queryWrapper);
		List<MappingStaticVo> mappingStaticList = new ArrayList();
		IPage<MappingStaticVo> mappingStaticPage = new Page<MappingStaticVo>();
		BeanUtils.copyProperties(mappingStatics, mappingStaticPage);
		for(MappingStatic mappingStatic : mappingStatics.getRecords()) {
			MappingStaticVo vo = new MappingStaticVo();
			BeanUtils.copyProperties(mappingStatic, vo);
			Map<Integer, Area> areaMap = iAreaService.getAreaMap();
			Map<Integer, Unit> unitMap = iUnitService.getUnitMap();
	        Area area = areaMap.get(mappingStatic.getSourceAreaId());
	        Unit unit = unitMap.get(mappingStatic.getSourceUnitId());
			vo.setSourceAreaName(area.getName());
			vo.setSourceUnitName(unit.getName());
			area = areaMap.get(mappingStatic.getMappingAreaId());
			unit = unitMap.get(mappingStatic.getMappingUnitId());
			vo.setMappingAreaName(area.getName());
			vo.setMappingUnitName(unit.getName());
			vo.setUserName(userComponent.getUserName(mappingStatic.getUserId()));
			mappingStaticList.add(vo);
		}
		mappingStaticPage.setRecords(mappingStaticList);
		res.setData(mappingStaticPage);
		res.setCode(HttpServletResponse.SC_OK);
    	return res;
    }
}
