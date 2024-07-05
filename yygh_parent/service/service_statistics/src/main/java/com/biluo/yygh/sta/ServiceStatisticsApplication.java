package com.biluo.yygh.sta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.biluo"})
@ComponentScan(basePackages = {"com.biluo"})
public class ServiceStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class, args);
    }

}

