package com.jwell56.security.cloud.service.asset;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@MapperScan(basePackages = "com.jwell56.security.cloud.service.asset.mapper")
@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@EnableFeignClients
public class ServiceAssetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAssetApplication.class, args);
    }

}
