package com.biluo.yygh.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
public class AliyunSmsConfig {
	private String ak;
	private String sk;
	private String signName;
	private String templateCode;
}
