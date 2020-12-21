package com.jwell56.security.cloud.service.asset.interceptors;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.utils.JwtUtils;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录拦截器
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AuthTokenInterceptor implements HandlerInterceptor {

    //这个方法是在访问接口之前执行的，我们只需要在这里写验证登陆状态的业务逻辑，就可以在用户调用指定接口之前验证登陆状态了
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("--------------进入service-asset拦截器-------------");

        response.setCharacterEncoding("UTF-8");
        String Authorization = request.getHeader("Authorization");
        String path = request.getRequestURI();

        log.info("访问路径：" + path);
        ResultObject resultObject = new ResultObject();
        OutputStream out = response.getOutputStream();
        response.setContentType("application/json; charset=UTF-8");

        Claims claims = JwtUtils.parseJWT(Authorization);
        if (claims != null) {
//            redisUtil.set("Authorization",claims.get("userJsonString", String.class),30*60);//30分钟
            //将用户信息放入threadLocal中,线程共享
            User userObject = JSONObject.parseObject(
                    claims.get("userJsonString", String.class), new TypeReference<User>() {});
            ThreadLocalUtil.getInstance().bind(userObject);
            return true;
        }
        resultObject.setCode(911);
        //token缺失即表示用户没有登录
        resultObject.setMsg("token缺失，用户未登录");
        out.write(JSON.toJSONString(resultObject).getBytes("UTF-8"));
		return true;

    }

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                @Nullable Exception ex) throws Exception {
    }

}

