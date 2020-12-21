package com.jwell56.security.cloud.service.role.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.role.entity.Log;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.mapper.LogMapper;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
public class GlobalOperationLog {

    @Autowired
    private LogMapper logMapper;

    public void saveOperationLog(JoinPoint joinPoint, ResultObject rvt) {

        //文艺青年的写法
        final Map<String, String> typeMap = new ImmutableMap.Builder<String, String>().
            put("save", "新增").
            put("add", "新增").
            put("delete","删除").
            put("update", "修改").
//            put("page", "查询").
//            put("paging", "查询").
//            put("find", "查询").
            put("upload","上传").
            put("download", "下载").
            put("exprotexcel", "下载").
            put("report", "下载").
            put("excel", "下载").build();
        
        try {
            String handleModule = "";
            String handleType = "";
            String userName = "";
            String des = "";
            Integer userId = 0;

            //获取用户名
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String Authorization = request.getHeader("Authorization");
            String handlerMethod = request.getMethod();
            String addr = request.getRemoteHost();
            String url = request.getRequestURI().toString();
            if(Authorization!=null&&!Authorization.isEmpty()) {
                Claims c = JwtUtils.parseJWT(Authorization);
                SysUser userObject = JSONObject.parseObject(c.get("userJsonString", String.class), new TypeReference<SysUser>() {
                });
                if (userObject != null) {
                    userName = userObject.getUsername();
                    userId = userObject.getUserId();
                    
                }
            }
            
            if(url.contains("/saveOrUpdate")) {
//            	String classType = joinPoint.getTarget().getClass().getName();
//            	String methodName = joinPoint.getSignature().getName();
            	// 参数值
//                BaseEntity demo1 = (BaseEntity)args[0];
//                Object test = demo1.getClass().newInstance();
//            	if() {}
//                BaseEntity demo  = (BaseEntity)args[0];
                if(rvt != null && rvt.getMsg() != null && rvt.getMsg().contains("新增")) {
                	handleType = "新增";
                  }else {
                  	handleType = "修改";
                  }  
            }else {
            	 for (String key : typeMap.keySet()) {
                     if (url.toLowerCase().contains(key)) {
                         handleType = typeMap.get(key);
                         break;
                     }
                 }
            }

            //获取模块
            //controller类对象
            Object controllerClass = joinPoint.getTarget();
            //根据类划分模块
            if (controllerClass.getClass().isAnnotationPresent(Api.class)) {
                Api api = controllerClass.getClass().getAnnotation(Api.class);
                handleModule = api.value();
                handleModule = handleModule.replaceAll("接口", "");
                handleModule = handleModule.replaceAll("集合", "");
                handleModule += "模块";
            }

            //获取操作类型
            //controller中的方法对象，MethodSignature是signature的子类
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            if (methodSignature.getMethod().isAnnotationPresent(RequestMapping.class)) {
//                RequestMapping methodRequestMapping = methodSignature.getMethod().getAnnotation(RequestMapping.class);
//                String[] classRequestMappingValues = methodRequestMapping.value();
//                if (classRequestMappingValues.length > 0) {
//                    String path = classRequestMappingValues[0];
////                    Object[] args = joinPoint.getArgs();
//                    if(path.equals("/saveOrUpdate")) {
////                    	String classType = joinPoint.getTarget().getClass().getName();
////                    	String methodName = joinPoint.getSignature().getName();
//                    	// 参数值
////                        BaseEntity demo1 = (BaseEntity)args[0];
////                        Object test = demo1.getClass().newInstance();
////                    	if() {}
////                        BaseEntity demo  = (BaseEntity)args[0];
//                        if(rvt.getMsg() != null && rvt.getMsg().contains("新增")) {
//                        	handleType = "新增";
//                          }else {
//                          	handleType = "修改";
//                          }  
//                    }else {
//                    	 for (String key : typeMap.keySet()) {
//                             if (path.toLowerCase().contains(key)) {
//                                 handleType = typeMap.get(key);
//                                 break;
//                             }
//                         }
//                    }
//                }
//            }

            //获取操作描述，即函数说明
            if (methodSignature.getMethod().isAnnotationPresent(ApiOperation.class)) {
                ApiOperation methodApiOperation = methodSignature.getMethod().getAnnotation(ApiOperation.class);
                des = methodApiOperation.value();
            }

            //只有能识别出类型的请求才记录日志
            if (!handleType.isEmpty() && !userName.isEmpty()) {
            	Log log = new Log();
            	log.setHandleModule(handleModule);
            	log.setHandleType(handleType);
            	log.setHandleUser(userName);
            	log.setHandleDes(des);
            	log.setHttpMethod(handlerMethod);
            	log.setRemoteAddr(addr);
//            	log.setUserId(userId);
            	logMapper.insert(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
