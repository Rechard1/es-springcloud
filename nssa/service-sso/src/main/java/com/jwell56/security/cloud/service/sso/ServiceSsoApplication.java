package com.jwell56.security.cloud.service.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@MapperScan(basePackages = "com.jwell56.security.cloud.service.sso.mapper")
@SpringBootApplication
@EnableSwagger2
@EnableZuulProxy
@EnableEurekaClient
public class ServiceSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSsoApplication.class, args);
    }

}
