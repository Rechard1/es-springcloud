package com.jwell56.security.cloud.service.ids;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@MapperScan(basePackages = "com.jwell56.security.cloud.service.ids.mapper")
@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@EnableFeignClients
public class ServiceIdsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceIdsApplication.class, args);
    }
}
