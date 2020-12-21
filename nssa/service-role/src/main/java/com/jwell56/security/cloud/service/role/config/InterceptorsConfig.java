package com.jwell56.security.cloud.service.role.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.jwell56.security.cloud.service.role.interceptors.AuthTokenInterceptor;

@Configuration
public class InterceptorsConfig implements WebMvcConfigurer {

    @Bean
    AuthTokenInterceptor authTokenInterceptor() {
        return new AuthTokenInterceptor();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("Content-Type", "X-Requested-With", "accept,Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "token")
                .allowedMethods("*")
                .allowedOrigins("*")
                .allowCredentials(true);
    }

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").
                addResourceLocations("classpath:/static/");//整合前端，对应开放静态页面访问
    }

    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        InterceptorRegistration interceptorRegistration = registry.addInterceptor(authTokenInterceptor());
        interceptorRegistration.addPathPatterns("/**");
        interceptorRegistration.excludePathPatterns("/vx/**",
                "/user/login","/role/unit/getRoleUnitList","/role/unit/getRoleUnitIds","/role/area/getRoleAreaList","/role/area/getRoleAreaIds","/user/code","/user/getUserById");

        //放行swagger访问页面
        interceptorRegistration.excludePathPatterns(
                "/swagger-resources/**",
                "/webjars/**", "/v2/**",
                "/swagger-ui.html/**");

        //整合前端，对应开放静态页面访问
        interceptorRegistration.excludePathPatterns(
                "/web/**",
                "/two/**",
                "/hgx/**",
                "/images/**",
                "/index.html/**",
                "/");
    }
}
