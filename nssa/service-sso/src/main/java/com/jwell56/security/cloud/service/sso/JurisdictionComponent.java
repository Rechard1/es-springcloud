package com.jwell56.security.cloud.service.sso;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.service.sso.common.JwtUtils;
import com.jwell56.security.cloud.service.sso.common.RedisUtil;
import com.jwell56.security.cloud.service.sso.entity.ModuleInterface;
import com.jwell56.security.cloud.service.sso.entity.RoleModule;
import com.jwell56.security.cloud.service.sso.entity.SysUser;
import com.jwell56.security.cloud.service.sso.service.IModuleInterfaceService;
import com.jwell56.security.cloud.service.sso.service.IModuleService;
import com.jwell56.security.cloud.service.sso.service.IRoleModuleService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口权限组件
 *
 * @author wsg
 * @since 2019/8/12
 */
@Component
@Configurable
@EnableScheduling
@EnableAsync
public class JurisdictionComponent {
    @Autowired
    IModuleInterfaceService iModuleInterfaceService;

    @Autowired
    IModuleService iModuleService;

    @Autowired
    IRoleModuleService iRoleModuleService;

    @Autowired
    RedisUtil redisUtil;

//    @Value("${zuul.routes.netsecurity}")
//    private String webPath;

    //TODO 权限限制
    public boolean pathIsPass(String path) {
        return true;
//        Integer roleId = getRoleIdByToken();
//        if (roleId == 0) {
//            return false;
//        }
//        List<ModuleInterface> moduleInterfaceList = getModuleInterfaceCache(roleId);
//        for (ModuleInterface moduleInterface : moduleInterfaceList) {
//            System.out.println(path);
//            if (path.startsWith(webPath.replace("/**", "") + moduleInterface.getBasePath())) {
//                return true;
//            } else if (path.startsWith(moduleInterface.getBasePath())) {
//                return true;
//            }
//        }
//        return false;
    }

    private List<ModuleInterface> getModuleInterface(Integer roleId) {
        List<ModuleInterface> moduleInterfaceList = new ArrayList<>();
        try {
            QueryWrapper<RoleModule> roleModuleQueryWrapper = new QueryWrapper<>();
            roleModuleQueryWrapper.lambda().eq(RoleModule::getRoleId, roleId);
            List<RoleModule> roleModuleList = iRoleModuleService.list(roleModuleQueryWrapper);
            List<Integer> roleModuleIdList = new ArrayList<>();
            for (RoleModule roleModule : roleModuleList) {
                roleModuleIdList.add(roleModule.getModuleId());
            }
            roleModuleIdList.add(0);//通用模块

            QueryWrapper<ModuleInterface> moduleInterfaceQueryWrapper = new QueryWrapper<>();
            moduleInterfaceQueryWrapper.lambda().in(ModuleInterface::getModuleId, roleModuleIdList);
            moduleInterfaceQueryWrapper.select("base_path");
            moduleInterfaceList = iModuleInterfaceService.list(moduleInterfaceQueryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moduleInterfaceList;
    }

    public List<ModuleInterface> getModuleInterfaceCache(Integer roleId, boolean setNew) {
        List<ModuleInterface> resultList;
        String cacheKey = "getModuleInterface-sso-" + roleId;
        if (redisUtil.get(cacheKey) == null || setNew) {
            resultList = this.getModuleInterface(roleId);
            redisUtil.set(cacheKey, resultList, 600);
        } else {
            resultList = (List<ModuleInterface>) redisUtil.get(cacheKey);
        }
        return resultList;
    }

    public List<ModuleInterface> getModuleInterfaceCache(Integer roleId) {
        return getModuleInterfaceCache(roleId, false);
    }

    private Integer getRoleIdByToken() {
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
}

