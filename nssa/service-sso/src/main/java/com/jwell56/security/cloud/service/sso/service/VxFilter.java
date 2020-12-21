//package com.jwell56.security.cloud.service.sso.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.jwell56.security.cloud.common.ResultObject;
//import com.jwell56.security.cloud.service.sso.entity.SysUserVx;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//
//@Component
//public class VxFilter implements Filter, Ordered {
//
//    @Autowired
//    private ISysUserVxService iSysUserVxService;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        String path = request.getRequestURI();
//        String vxPath = "/vx";
//        if(path.indexOf(vxPath) > 0){
//            String openid = request.getHeader("openid");
//            response.setContentType("application/json; charset=UTF-8");
//            //验证openid是否携带
//            if (StringUtils.isEmpty(openid)) {
//                ResultObject resultObject = new ResultObject();
//                resultObject.setCode(911);
//                //token缺失即表示用户没有登录
//                resultObject.setMsg("openid缺失");
//                response.setStatus(500);
//            } else {
//                //验证openid与userId是否绑定
//                String method = request.getMethod();
//
//                String userId = null;
//                if(method.equals("GET")){
//                    userId = request.getParameter("userid");
//                }
//                if(method.equals("POST")){
//                    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
//                    StringBuffer sb=new StringBuffer();
//                    String s=null;
//                    while((s=br.readLine())!=null){
//                        sb.append(s);
//                    }
//                    JSONObject jsonObject = JSONObject.parseObject(sb.toString());
//                    userId = jsonObject.getString("userid");
//                }
//
//                if(userId != null){
//                    QueryWrapper<SysUserVx> queryWrapper = new QueryWrapper<>();
//                    queryWrapper.lambda().eq(SysUserVx::getOpenid,openid);
//                    queryWrapper.lambda().eq(SysUserVx::getUserid,userId);
//                    SysUserVx sysUserVx = iSysUserVxService.getOne(queryWrapper);
//                    if(sysUserVx == null){
//                        ResultObject resultObject = new ResultObject();
//                        resultObject.setCode(911);
//                        resultObject.setMsg("openid与用户id不匹配");
//                        response.setStatus(500);
//                    }
//                }
//            }
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
