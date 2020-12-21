package com.jwell56.security.cloud.service.role.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.role.entity.Role;
import com.jwell56.security.cloud.service.role.entity.RoleArea;
import com.jwell56.security.cloud.service.role.entity.RoleModule;
import com.jwell56.security.cloud.service.role.entity.RoleUnit;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.entity.dto.RoleDto;
import com.jwell56.security.cloud.service.role.entity.vo.RoleVo;
import com.jwell56.security.cloud.service.role.service.IRoleAreaService;
import com.jwell56.security.cloud.service.role.service.IRoleModuleService;
import com.jwell56.security.cloud.service.role.service.IRoleService;
import com.jwell56.security.cloud.service.role.service.IRoleUnitService;
import com.jwell56.security.cloud.service.role.service.ISysUserService;
import com.jwell56.security.cloud.service.role.utils.JwtUtils;
import com.jwell56.security.cloud.service.role.utils.RedisUtil;
import com.jwell56.security.cloud.service.role.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.role.utils.ThreadLocalUtil;
import com.jwell56.security.cloud.service.role.validated.AddRole;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 角色单位表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private IRoleAreaService iRoleAreaService;

    @Autowired
    private IRoleUnitService iRoleUnitService;
    
    @Autowired
    private IRoleModuleService iRoleModuleService;
    
    @Autowired
    private IRoleService roleService;
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "角色区域列表", notes = "可以只传token，roleId则为token对应的roleId")
    @RequestMapping(value = "/areaList", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "roleId", value = "角色id", dataType = "Integer"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token")
    })
    public ResultObject areaList(Integer roleId) {
        try {
            List<Integer> roleAreaIdList = new ArrayList<>();
//            if (roleId == null || roleId == 0) {
//                roleId = getRoleIdByToken();
//            }
            if (roleId != null && roleId != 0) {
                List<RoleArea> roleAreaList = iRoleAreaService.roleAreaCache();
                for (RoleArea roleArea : roleAreaList) {
                    if (roleArea.getRoleId().equals(roleId) && !roleAreaIdList.contains(roleArea.getAreaId())) {
                        roleAreaIdList.add(roleArea.getAreaId());
                    }
                }
            }
            return ResultObject.data(roleAreaIdList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation(value = "角色区域列表", notes = "可以只传token，roleId则为token对应的roleId")
    @RequestMapping(value = "/areaListByToken", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "token", value = "用户token", dataType = "String"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token")
    })
    public ResultObject areaListByToken(String token) {
        try {
            return areaList(getRoleIdByToken(token));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation(value = "角色单位列表", notes = "可以只传token，roleId则为token对应的roleId")
    @RequestMapping(value = "/unitListByToken", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "token", value = "用户token", dataType = "String"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token")
    })
    public ResultObject unitListByToken(String token) {
        try {
            return unitList(getRoleIdByToken(token));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

//    public Integer getRoleIdByToken() {
//        try {
//            ServletRequestAttributes servletRequestAttributes =
//                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = servletRequestAttributes.getRequest();
//            String Authorization = request.getHeader("Authorization");
//            if (Authorization != null && !Authorization.isEmpty()) {
//                Claims c = JwtUtils.parseJWT(Authorization);
//                SysUser userObject = JSONObject.parseObject(c.get("userJsonString", String.class), new TypeReference<SysUser>() {
//                });
//                if (userObject != null) {
//                    return userObject.getRoleId();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

    public Integer getRoleIdByToken(String token) {
        try {
            if (token != null && !token.isEmpty()) {
                Claims c = JwtUtils.parseJWT(token);
                SysUser userObject = JSONObject.parseObject(c.get("userJsonString", String.class), new TypeReference<SysUser>() {
                });
                if (userObject != null) {
                    return userObject.getRoleId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    @ApiOperation(value = "角色单位列表", notes = "可以只传token，roleId则为token对应的roleId")
    @RequestMapping(value = "/unitList", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "roleId", value = "角色id", dataType = "Integer"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token")
    })
    public ResultObject unitList(Integer roleId) {
        try {
            List<Integer> roleUnitIdList = new ArrayList<>();
//            if (roleId == null || roleId == 0) {
//                roleId = getRoleIdByToken();
//            }
            if (roleId != null && roleId != 0) {
                List<RoleUnit> roleAreaList = iRoleUnitService.roleUnitCache();
                for (RoleUnit roleUnit : roleAreaList) {
                    if (roleUnit.getRoleId().equals(roleId) && !roleUnitIdList.contains(roleUnit.getUnitId())) {
                        roleUnitIdList.add(roleUnit.getUnitId());
                    }
                }
            }
            return ResultObject.data(roleUnitIdList);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("新增角色")
    @Transactional
    @PostMapping("add")
    public ResultObject add(@Validated({AddRole.class}) @RequestBody RoleDto roleDto) {
    	ResultObject res = new ResultObject();
    	Role role = new Role();
    	BeanUtils.copyProperties(roleDto.getRole(), role);
    	//获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        role.setRoleType(3);
        role.setCreatorId(userInfo.getUserId());
//        role.setRoleDesc(roleDto.getRoleDesc());
//        role.setRoleName(roleDto.getRoleName());
        if(userInfo.getRoleType() == 1) {
        	role.setEnterpriseId(userInfo.getEnterpriseFlag());
        }else if(userInfo.getRoleType() == 2) {
        	role.setEnterpriseId(userInfo.getEnterpriseId());
        }
        boolean b = roleService.save(role);
        roleService.saveRole(roleDto.getUnitList(), roleDto.getAreaList(), roleDto.getModuleList(), role.getRoleId());
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增角色成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增角色失败");
        }
		return res;
    }
    
    @ApiOperation("删除角色")
	@DeleteMapping("delete")
	public ResultObject delete(String roleIds) {
		ResultObject res = new ResultObject();
		List<Integer> roleIdList = StringIdsUtil.listIds(roleIds);
		List<SysUser> users = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for(Integer roleId : roleIdList) {
			QueryWrapper<SysUser> wrapper = new QueryWrapper<SysUser>();
			wrapper.lambda().eq(SysUser :: getRoleId, roleId);
			List<SysUser> user = userService.list(wrapper);
			if(!user.isEmpty()){
				users.addAll(user);
				Role role = roleService.getById(roleId);
				sb.append(role.getRoleName());
				sb.append("，");
			}
		}
		if(!users.isEmpty()) {
			sb.append("已被用户使用，请先删除用户，或者更换用户角色！！");
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg(sb.toString());
            return res;
		}
		boolean b = false;
		b = roleService.removeByIds(roleIdList);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除角色成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除角色失败");
        }
		return res;
	}
    
    @ApiOperation("修改角色")
    @Transactional
	@PostMapping("update")
	public ResultObject update(@Validated({AddRole.class}) @RequestBody RoleDto roleDto) {
		ResultObject res = new ResultObject();
		boolean b = false;
		QueryWrapper<RoleArea> roleAreaWrapper = new QueryWrapper<RoleArea>();
		QueryWrapper<RoleUnit> roleUnitWrapper = new QueryWrapper<RoleUnit>();
		QueryWrapper<RoleModule> roleModuleWrapper = new QueryWrapper<RoleModule>();
		roleAreaWrapper.lambda().eq(RoleArea :: getRoleId, roleDto.getRole().getRoleId());
		roleUnitWrapper.lambda().eq(RoleUnit :: getRoleId, roleDto.getRole().getRoleId());
		roleModuleWrapper.lambda().eq(RoleModule :: getRoleId, roleDto.getRole().getRoleId());
		b = roleService.updateById(roleDto.getRole());
		//删除各个表中的role权限
		iRoleAreaService.remove(roleAreaWrapper);
		iRoleUnitService.remove(roleUnitWrapper);
		iRoleModuleService.remove(roleModuleWrapper);
		//新增权限
		roleService.saveRole(roleDto.getUnitList(), roleDto.getAreaList(), roleDto.getModuleList(), roleDto.getRole().getRoleId());
		redisUtil.del("roleUnit_" + roleDto.getRole().getRoleId());
		redisUtil.del("roleArea_" + roleDto.getRole().getRoleId());
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("修改角色成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改角色失败");
        }
		return res;
	}
    
    /**
     * 角色分页
     *
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @ApiOperation(value = "角色分页", notes = "角色分页")
    @RequestMapping(value = {"/paging"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "keyword", value = "搜索关键字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "页长", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "页码", dataType = "Integer")}
    )
    public ResultObject paging(Integer pageNum, Integer pageSize, String keyword) {

        ResultObject resultObject = new ResultObject();
        //获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();

            if (keyword != null && !"".equals(keyword)) {
                roleQueryWrapper.lambda().like(Role::getRoleName, keyword.trim());
            }
            roleQueryWrapper.lambda().eq(Role :: getEnterpriseId, userInfo.getEnterpriseId()).ne(Role :: getRoleType, 2).orderByDesc(Role::getCreateTime);
            IPage iPage = new Page(pageNum, pageSize);
            IPage<Role> moduleIPage = roleService.page(iPage, roleQueryWrapper);
            IPage<RoleVo> resPage = new Page<RoleVo>();
            List<RoleVo> voList = new ArrayList<RoleVo>();
            BeanUtils.copyProperties(moduleIPage, resPage);
            for(Role role : moduleIPage.getRecords()) {
            	RoleVo vo = new RoleVo();
            	BeanUtils.copyProperties(role, vo);
            	vo.setUserName(userService.getById(role.getCreatorId()).getUsername());
            	voList.add(vo);
            }
            resPage.setRecords(voList);
            resultObject.setData(resPage);
            resultObject.setCode(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }
    
    /**
     * 角色详情
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "角色详情", notes = "根据角色ID查询角色详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "角色ID", required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject detail(@RequestParam(value = "roleId") Integer roleId) {

        ResultObject resultObject = new ResultObject();
        try {
            Map<String, Object> dataMap = null;

            if (roleId != null) {
                dataMap = roleService.detail(roleId);
                Role role = roleService.getById(roleId);
                dataMap.put("role", role);
            }
            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setData(dataMap);

        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        return resultObject;
    }
    
    @GetMapping("list")
    public ResultObject list() {
    	ResultObject res = new ResultObject();
    	//获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<Role> roleWrapper = new QueryWrapper<Role>();
        roleWrapper.lambda().eq(Role :: getEnterpriseId, userInfo.getEnterpriseId());
        
        List<Role> roleList = roleService.list(roleWrapper);
    	
        res.setData(roleList);
    	res.setCode(HttpServletResponse.SC_OK);
    	return res;
    }
}
