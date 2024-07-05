package com.biluo.yygh.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wx.open")
@Data
public class WxConfig {
	private String appId;
	private String appSecret;
	private String redirectUrl;
	private String baseUrl;
}
