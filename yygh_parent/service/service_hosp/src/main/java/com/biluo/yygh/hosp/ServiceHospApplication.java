package com.biluo.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.biluo")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.biluo.yygh.cmn")
public class ServiceHospApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServiceHospApplication.class, args);
	}
}
