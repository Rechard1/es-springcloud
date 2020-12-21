package com.jwell56.security.cloud.service.sso.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.sso.common.JwtUtils;
import com.jwell56.security.cloud.service.sso.entity.SysUser;
import com.jwell56.security.cloud.service.sso.entity.UserDto;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wsg
 * @since 2019/12/30
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation("获取用户id")
    @RequestMapping(value = "/userId", method = RequestMethod.GET)
    @ApiImplicitParam(paramType = "query", name = "token", value = "token", dataType = "String")
    public ResultObject login(String token) {
        try {
            if (token != null && !token.isEmpty()) {
                Claims c = JwtUtils.parseJWT(token);
                SysUser userObject = JSONObject.parseObject(c.get("userJsonString", String.class), new TypeReference<SysUser>() {
                });
                if (userObject != null) {
                    return ResultObject.data(userObject);
                }
            }
            return ResultObject.data(null);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
}
