package com.jwell56.security.cloud.service.sso.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.sso.common.JwtUtils;
import com.jwell56.security.cloud.service.sso.entity.RoleArea;
import com.jwell56.security.cloud.service.sso.entity.RoleUnit;
import com.jwell56.security.cloud.service.sso.entity.SysUser;
import com.jwell56.security.cloud.service.sso.service.IRoleAreaService;
import com.jwell56.security.cloud.service.sso.service.IRoleUnitService;

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
    IRoleAreaService iRoleAreaService;

    @Autowired
    IRoleUnitService iRoleUnitService;


    @ApiOperation(value = "角色区域列表", notes = "可以只传token，roleId则为token对应的roleId")
    @RequestMapping(value = "/areaList", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "roleId", value = "角色id", dataType = "Integer"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token")
    })
    public ResultObject areaList(Integer roleId) {
        try {
            List<Integer> roleAreaIdList = new ArrayList<>();
            if (roleId == null || roleId == 0) {
                roleId = getRoleIdByToken();
            }
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

    public Integer getRoleIdByToken() {
        try {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String Authorization = request.getHeader("Authorization");
            if (Authorization != null && !Authorization.isEmpty()) {
                Claims c = JwtUtils.parseJWT(Authorization);
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
            if (roleId == null || roleId == 0) {
                roleId = getRoleIdByToken();
            }
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
}
