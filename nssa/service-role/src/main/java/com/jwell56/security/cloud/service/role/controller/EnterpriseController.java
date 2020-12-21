package com.jwell56.security.cloud.service.role.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
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
import com.jwell56.security.cloud.service.role.entity.Enterprise;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.entity.vo.EnterpriseVo;
import com.jwell56.security.cloud.service.role.service.IEnterpriseService;
import com.jwell56.security.cloud.service.role.service.ISysUserService;
import com.jwell56.security.cloud.service.role.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.role.utils.ThreadLocalUtil;
import com.jwell56.security.cloud.service.role.validated.AddEnterprise;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "EnterpriseController", description = "企业接口集合")
@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {

	@Autowired
	private IEnterpriseService enterpriseService;
	
	@Autowired
	private ISysUserService userService;
	
	@Transactional
	@ApiOperation("新增企业")
	@PostMapping("add")
	public ResultObject add(@Validated({AddEnterprise.class}) @RequestBody Enterprise enterprise) {
		ResultObject res = new ResultObject();
		//获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        if(userInfo.getRoleType() != 1) {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
        	res.setMsg("无新增企业权限！！");
        	return res;
        }
        QueryWrapper<Enterprise> wrapper1 = new QueryWrapper<Enterprise>();
        wrapper1.lambda().eq(Enterprise :: getEnterpriseName, enterprise.getEnterpriseName());
        Enterprise en = enterpriseService.getOne(wrapper1);
        if(en != null) {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
        	res.setMsg("出现重名，请重新命名！");
        	return res;
        }
        enterprise.setCreatorId(userInfo.getUserId());
        boolean b = enterpriseService.save(enterprise);
        QueryWrapper<SysUser> wrapper = new QueryWrapper<SysUser>();
        wrapper.lambda().eq(SysUser :: getRoleType, 1);
        List<SysUser> users = userService.list(wrapper);
        for(SysUser user : users) {
        	if(user.getEnterpriseId() == 0) {
        		user.setEnterpriseId(enterprise.getEnterpriseId());
        	}
        }
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增企业成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增企业失败");
        }
		return res;
	}
	
	@Transactional
	@ApiOperation("删除企业")
	@DeleteMapping("delete")
	public ResultObject delete(String enterpriseIds) {
		ResultObject res = new ResultObject();
		//获取删除的企业Id
		List<Integer> idList = StringIdsUtil.listIds(enterpriseIds);
		for(Integer enterpriseId : idList) {
			enterpriseService.delete(enterpriseId);
		}
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
        res.setMsg("删除企业成功");		
		return res;
	}
	
	@ApiOperation("修改企业")
	@PostMapping("update")
	public ResultObject update(@RequestBody @Validated({AddEnterprise.class}) Enterprise enterprise) {
		ResultObject res = new ResultObject();
		boolean b = false;
		b = enterpriseService.updateById(enterprise);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("修改企业成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改企业失败");
        }
		return res;
	}
	
	@ApiOperation("企业详情")
	@GetMapping("detail")
	public ResultObject detail(Integer enterpriseId) {
		ResultObject res = new ResultObject();
		Enterprise enterprise = enterpriseService.getById(enterpriseId);
		res.setData(enterprise);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
	
	@ApiOperation("企业详情页")
	@GetMapping("paging")
	public ResultObject paging(Integer pageSize, Integer pageNum, String keyword) {
		ResultObject res = new ResultObject();
		
		QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();
		if(!StringUtils.isEmpty(keyword)) {
			queryWrapper.lambda().like(Enterprise :: getEnterpriseName, keyword.trim());
		}
		queryWrapper.lambda().orderByDesc(Enterprise :: getCreateTime);
		
		IPage iPage = new Page(pageNum, pageSize);
		IPage<Enterprise> enterprisePage = enterpriseService.page(iPage, queryWrapper);
		IPage<EnterpriseVo> resPage = new Page<EnterpriseVo>();
		List<EnterpriseVo> voList = new ArrayList<EnterpriseVo>();
		BeanUtils.copyProperties(enterprisePage, resPage);
		for(Enterprise enterprise : enterprisePage.getRecords()) {
			EnterpriseVo vo = new EnterpriseVo();
			BeanUtils.copyProperties(enterprise, vo);
			SysUser user = userService.getById(enterprise.getCreatorId());
			vo.setCreatorName(user.getUsername());
			voList.add(vo);
		}
		resPage.setRecords(voList);
//		List<Enterprise> enterpriseList = enterpriseService.list(queryWrapper);
		res.setData(resPage);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
	
	@GetMapping("admin")
	public ResultObject admin() {
		ResultObject res = new ResultObject();
		//获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        Enterprise en = new Enterprise();
        if(userInfo.getRoleType() == 1 && userInfo.getEnterpriseFlag() != 0) {
        	en = enterpriseService.getById(userInfo.getEnterpriseFlag());
        }
        res.setData(en);
        return res;
	}
	
	@GetMapping("list")
	public ResultObject list() {
		ResultObject res = new ResultObject();
		List<Enterprise> resList = enterpriseService.list(null);
		res.setCode(HttpServletResponse.SC_OK);
		res.setData(resList);
		return res;
	}
	
	@GetMapping("switch")
	public ResultObject switchEnterprise(Integer enterpriseId) {
		ResultObject res = new ResultObject();
		//获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        userInfo.setEnterpriseId(enterpriseId);
        
        boolean b = userService.updateById(userInfo);
        
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("切换企业成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("切换企业失败");
        }
		return res;
        
	}
}
