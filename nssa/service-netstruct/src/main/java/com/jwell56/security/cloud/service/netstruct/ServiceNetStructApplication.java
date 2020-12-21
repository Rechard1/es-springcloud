package com.jwell56.security.cloud.service.netstruct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@MapperScan(basePackages = "com.jwell56.security.cloud.service.netstruct.mapper")
@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
@EnableDiscoveryClient
public class ServiceNetStructApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceNetStructApplication.class, args);
    }

}
