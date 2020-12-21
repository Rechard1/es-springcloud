package com.jwell56.security.cloud.service.role;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @since 2020/5/7
 * @author ql
 * @version 1.0.0
 */

@MapperScan(basePackages = "com.jwell56.security.cloud.service.role.mapper")
@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceRoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRoleApplication.class, args);
    }

}
