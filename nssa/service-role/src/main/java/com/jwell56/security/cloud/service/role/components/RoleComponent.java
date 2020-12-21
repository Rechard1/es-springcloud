package com.jwell56.security.cloud.service.role.components;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.service.IRoleAreaService;
import com.jwell56.security.cloud.service.role.service.IRoleUnitService;
import com.jwell56.security.cloud.service.role.utils.JwtUtils;

import io.jsonwebtoken.Claims;

/**
 * 角色权限组件
 *
 * @author wsg
 * @since 2019/6/19
 */
@Component
@Configurable
@EnableScheduling
@EnableAsync
public class RoleComponent {

    @Autowired
    IRoleAreaService iRoleAreaService;

    @Autowired
    IRoleUnitService iRoleUnitService;

    private Integer getRoleIdByToken() {
        try {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String Authorization = request.getHeader("Authorization");
            if (Authorization != null && !Authorization.isEmpty()) {
                Claims c = JwtUtils.parseJWT(Authorization);
                SysUser userObject = JSONObject.parseObject(
                        c.get("userJsonString", String.class), new TypeReference<SysUser>() {});
                if (userObject != null) {
                    return userObject.getRoleId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> getRoleAreaIdList() {
        return this.getRoleAreaIdListByRoleId(this.getRoleIdByToken());
    }


    private List<Integer> getRoleAreaIdListByRoleId(Integer roleId) {
        try {
            return iRoleAreaService.getRoleAreaIdList(roleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Integer> getRoleUnitIdList() {
        return getRoleUnitIdListByRoleId(this.getRoleIdByToken());
    }

    private List<Integer> getRoleUnitIdListByRoleId(Integer roleId) {
        try {
            return iRoleUnitService.getRoleUnitList(roleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}