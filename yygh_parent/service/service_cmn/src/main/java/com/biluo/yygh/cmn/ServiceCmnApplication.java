package com.biluo.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.biluo")
@EnableDiscoveryClient
public class ServiceCmnApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServiceCmnApplication.class, args);
	}
}
