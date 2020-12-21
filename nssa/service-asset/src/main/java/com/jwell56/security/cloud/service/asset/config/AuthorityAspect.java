package com.jwell56.security.cloud.service.asset.config;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.utils.GlobalOperationLog;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class AuthorityAspect {

	@Resource
    private GlobalOperationLog globalOperationLog;
	
//	@Pointcut("execution(public * com.jwell56.security.cloud.service.snmp.controller..*.*(..))")
//	public void auth() {}
//	
//	@Before("auth()")
//    public void doBefore(JoinPoint point) throws Throwable {
//		
//		//检查是否登录
//		User userInfo = ThreadLocalUtil.getInstance().getUserInfo(); 
//		ThreadLocalUtil.getInstance().bind(userInfo);
//		if(userInfo == null) return;
//		//获取请求接口
//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//		String url = request.getRequestURI().toString();
//		//防重
//		url = url.replaceAll("//", "/");
//		log.info("访问接口为"+ url);
        
//		//判断其为登录
//		if(url.contains("common")) {
//			return;
//        }
//		
//		List<String> permissionList = Arrays.asList(userDao.getPermission(userInfo.getId()).split(","));
//		//超级管理员权限
//		if(permissionList.size() == 1) {
//			if(permissionList.get(0).equals("/**"))
//				return;
//		}
//		
//		for(String per : permissionList) {
//			if(AuthUtils.isPermission(per, url)) return;
//		}
//		permissionList.stream().forEach(x -> {
//			if(AuthUtils.isPermission(x, url)) {
//				return;
//			}
//		});
//		response.sendRedirect("/common/auth/noPermission");
//		point.proceed();
//		throw new AssessDeniedException("您无权操作！");
//    }
	
	@AfterReturning(returning = "rvt", pointcut = "execution(public * com.jwell56.security.cloud.service.asset.controller..*.*(..))")
	public void writeLog(JoinPoint joinPoint,Object rvt) {
		ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
		
        if(rvt instanceof ResultObject) {
        	ResultObject res = (ResultObject) rvt;
        	if(res.getCode().intValue() == 200) {
        		globalOperationLog.saveOperationLog(joinPoint, res);
        	}
        }
        if(request.getRequestURI().toString().contains("exprotExcel")) {
			//记录操作日志
	        globalOperationLog.saveOperationLog(joinPoint, null);
		}
	}
}
