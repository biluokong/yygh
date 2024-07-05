package com.biluo.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.biluo.yygh.user.mapper")
public class UserConfig {
}
