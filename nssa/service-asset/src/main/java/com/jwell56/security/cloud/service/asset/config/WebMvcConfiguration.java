package com.jwell56.security.cloud.service.asset.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.alibaba.fastjson.JSON;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * 配置一个虚拟的文件服务器地址
 */
@SuppressWarnings("deprecation")
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		// 自定义异常处理器一般请放在首位
        exceptionResolvers.add(0, new AbstractHandlerExceptionResolver() {
            @Override
            protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                response.setContentType("application/json;charset=UTF-8");
//                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                Map map = new HashMap();
                try {
//                    String jsonStr = "";
                    if (ex instanceof ExpiredJwtException) {
                        response.setStatus(HttpStatus.OK.value());
                        map.put("code", 911);
                        map.put("msg", "token过期，请重新登录！");
//                        String res = (String) ;
//                        jsonStr = "{'code':203,'msg':'您无权操作'}";
                    }else if(ex instanceof HttpMessageNotReadableException){
                    	response.setStatus(HttpStatus.OK.value());
                        map.put("code", 400);
                        map.put("msg", "传入参数不能为空！");
                    }else if(ex instanceof MethodArgumentNotValidException){
                    	response.setStatus(HttpStatus.OK.value());
                        map.put("code", 400);
                        map.put("msg", "传入参数错误！");
                    }else {
//                        jsonStr = "{'code':500,'msg':'服务器未知异常'}";
                	response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    map.put("code", 500);
                    map.put("msg", "服务器未知异常");
                    }
                    response.getOutputStream().write(JSON.toJSONBytes(map));
                    response.getOutputStream().flush();
                    response.getOutputStream().close();
                   
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
	}
}
