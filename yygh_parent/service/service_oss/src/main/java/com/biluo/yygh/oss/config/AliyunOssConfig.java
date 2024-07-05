package com.biluo.yygh.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@RefreshScope
public class AliyunOssConfig {
	private String endpoint;
	private String ak;
	private String sk;
	private String bucket;
}
