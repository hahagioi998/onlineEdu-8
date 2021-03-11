package com.online.edu.service.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//扫描所有的包，在edu下的类都扫描，方便后续的微服务架构
@ComponentScan("com.online.edu")
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceEduApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceEduApplication.class, args);
    }
}
