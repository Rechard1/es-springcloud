package com.jwell56.security.cloud.service.sso;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.sso.common.JwtUtils;
import com.jwell56.security.cloud.service.sso.common.RedisUtil;
import com.jwell56.security.cloud.service.sso.entity.SysUser;
import com.jwell56.security.cloud.service.sso.entity.SysUserVx;
import com.jwell56.security.cloud.service.sso.service.ISysUserVxService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author wsg
 * @since 2019/10/23
 */
@Component
public class MyFilter extends ZuulFilter {
    private static Logger logger = LoggerFactory.getLogger(MyFilter.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysUserVxService iSysUserVxService;

    @Autowired
    private JurisdictionComponent jurisdictionComponent;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        logger.info("--------------进入拦截器-------------");
        response.setCharacterEncoding("UTF-8");

        String Authorization = request.getHeader("Authorization");

        String path = request.getRequestURI();

        //放行请求
        String[] excludePathPatterns = new String[]{"/code", "/login", "/download", "/showImage", "/decodeUserInfo"};
        for (String excludePath : excludePathPatterns) {
            if (path.indexOf(excludePath) > 0) {
                return null;
            }
        }

        String vxPath = "/vx";
        if (path.indexOf(vxPath) > 0) {
            String openid = request.getHeader("openid");
            response.setContentType("application/json; charset=UTF-8");
            //验证openid是否携带
            if (StringUtils.isEmpty(openid)) {
                ResultObject resultObject = new ResultObject();
                resultObject.setCode(911);
                //token缺失即表示用户没有登录
                resultObject.setMsg("openid缺失");
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(200);
                ctx.setResponseBody(JSON.toJSONString(resultObject));
            } else {
                //验证openid与userId是否绑定
                String method = request.getMethod();

                String userId = null;
                if (method.equals("GET")) {
                    userId = request.getParameter("userid");
                }
                if (method.equals("POST")) {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                        StringBuffer sb = new StringBuffer();
                        String s = null;
                        while ((s = br.readLine()) != null) {
                            sb.append(s);
                        }
                        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
                        userId = jsonObject.getString("userid");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (userId != null) {
                    QueryWrapper<SysUserVx> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(SysUserVx::getOpenid, openid);
                    queryWrapper.lambda().eq(SysUserVx::getUserid, userId);
                    SysUserVx sysUserVx = iSysUserVxService.getOne(queryWrapper);
                    if (sysUserVx == null) {
                        ResultObject resultObject = new ResultObject();
                        resultObject.setCode(911);
                        resultObject.setMsg("openid与用户id不匹配");
                        ctx.setSendZuulResponse(false);
                        ctx.setResponseStatusCode(200);
                        ctx.setResponseBody(JSON.toJSONString(resultObject));
                    }
                }
            }
            return null;
        }


        try {
            response.setContentType("application/json; charset=UTF-8");
            //接口权限限制
            if (!jurisdictionComponent.pathIsPass(path)) {
                ResultObject resultObject = new ResultObject();
                resultObject.setCode(HttpServletResponse.SC_FORBIDDEN);
                resultObject.setSuccess(false);
                resultObject.setMsg("用户无该接口访问权限！");
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(200);
                ctx.setResponseBody(JSON.toJSONString(resultObject));
            }

            //验证token
            if (StringUtils.isEmpty(Authorization)) {

                ResultObject resultObject = new ResultObject();
                resultObject.setCode(911);
                //token缺失即表示用户没有登录
                resultObject.setMsg("token缺失，用户未登录");
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(200);
                ctx.setResponseBody(JSON.toJSONString(resultObject));

            } else {
                //使用jwt工具中验证方法来验证签名
                Claims claims = JwtUtils.parseJWT(Authorization);

                if (claims != null) {
                    SysUser sysUser = JSONObject.parseObject(claims.get("userJsonString", String.class), new TypeReference<SysUser>() {
                    });
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    if (parameterMap.get("userId") != null && parameterMap.get("userId").length > 0 &&
                            Integer.parseInt(parameterMap.get("userId")[0]) != sysUser.getUserId()) {
                        ctx.setSendZuulResponse(false);
                        ctx.setResponseStatusCode(200);
                        ctx.setResponseBody(JSON.toJSONString(ResultObject.badRequest("非法的userId")));
                    }
                    redisUtil.set("Authorization", claims.get("userJsonString", String.class), 30 * 60);//30分钟
                    return null;
                }
            }
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
        return null;
    }
}
