package com.jwell56.security.cloud.service.sso.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.sso.common.*;
import com.jwell56.security.cloud.service.sso.entity.RoleModuleVo;
import com.jwell56.security.cloud.service.sso.entity.SysUser;
import com.jwell56.security.cloud.service.sso.entity.TreeNode;
import com.jwell56.security.cloud.service.sso.entity.UserDto;
import com.jwell56.security.cloud.service.sso.service.IRoleModuleService;
import com.jwell56.security.cloud.service.sso.service.ISysUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-10-29
 */
@RestController
@RequestMapping("/backend/users")
public class SysUserController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ISysUserService iSysUserService;

    @Autowired
    IRoleModuleService iRoleModuleService;

    @ApiOperation(value = "用户登录", notes = "用户输入账号密码登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResultObject login(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestBody UserDto user) throws ServletException, IOException {
        String code = user.getCode();

        if (code == null || StringUtils.isEmpty(code)) {
            return ResultObject.badRequest("验证码不能为空");
        }

        if (!redisUtil.hasKey(code)) {
            return ResultObject.badRequest("验证码错误！");
        }

        if (redisUtil.getExpire(code) == 0) {
            return ResultObject.badRequest("验证码过期！");
        }

        if (user.getUsername() == null || user.getUsername().equals("") ||
                user.getPassword() == null || user.getPassword().equals("")) {
            return ResultObject.badRequest("用户名和账号不能为空");
        }

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, user.getUsername())
                .and(e -> e.eq(SysUser::getPassword, MD5Utils.getMD5(user.getPassword())));

        SysUser userLogin = iSysUserService.getOne(queryWrapper);

        if (userLogin != null) {

            //生成token字符串
            String jwtToken = JwtUtils.createJWT("jwt",
                    "{id:10086,name:Ronnie}", userLogin);

            response.addHeader("Authorization", jwtToken);
            //保存用户到redis,保存时长是30分钟
            redisUtil.set(jwtToken, JSONObject.toJSONString(userLogin), 30 * 60);

            Map<String, Object> map = new HashMap<>();
            map.put("token", jwtToken);
            map.put("user", userLogin);

            //获取用户配置的角色访问模块
            if (userLogin.getRoleId() != null) {
                List<RoleModuleVo> roleModuleVoList = iRoleModuleService.getRoleModuleVoList(userLogin.getRoleId());
                List<TreeNode> treeNodeList = new LinkedList<>();
                for (RoleModuleVo roleModuleVo : roleModuleVoList) {
                    TreeNode treeNode = new TreeNode(roleModuleVo.getRoleModuleId(),
                            roleModuleVo.getPId(),
                            roleModuleVo.getModuleName(),
                            roleModuleVo.getBasePath());
                    treeNodeList.add(treeNode);
                }
                map.put("userModuleList", TreeNodeUtils.getTreeList(treeNodeList, 0));
                redisUtil.set("userModuleList:" + userLogin.getUserId(), JSONObject.toJSONString(TreeNodeUtils.getTreeList(treeNodeList, 0)));

            } else {
                return ResultObject.badRequest("用户未设置角色，请先设置正确角色！");
            }

            map.put("rootPath","/home");

            return ResultObject.data(map, "登录成功");
        } else {
            return ResultObject.message("登录失败,账号或者密码错误");
        }
    }

    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @RequestMapping(value = "/code", name = "name", method = RequestMethod.GET)
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
            }
    )
    public void getCode(HttpServletRequest request, HttpServletResponse response) {

        ResultObject resultObject = new ResultObject();

        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();
        //以秒为单位，即在没有活动30分钟后，session将失效
        session.setMaxInactiveInterval(30 * 60);

        //调用工具类生成验证码
        String verifyCode = VerifyCodeUtils.generateNumberVerifyCode(4);
        //session.setAttribute("code", verifyCode);
        //保存到redis,并设置时长为10分钟
        redisUtil.set(verifyCode, verifyCode, 10 * 60);

        try {
            VerifyCodeUtils.outputImage(100, 38, response.getOutputStream(), verifyCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "注销登录", notes = "用户注销登录退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject logout(HttpServletRequest req, HttpServletResponse resp) {

        ResultObject resultObject = new ResultObject();

        try {
            String authHeader = req.getHeader("Authorization");
            Boolean b = redisUtil.expire(authHeader,0);
            if (b) {
                resultObject.setMsg("退出成功!");
                resultObject.setCode(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultObject.setMsg("退出失败，请联系管理员");
            e.printStackTrace();
        }
        return resultObject;

    }
}
