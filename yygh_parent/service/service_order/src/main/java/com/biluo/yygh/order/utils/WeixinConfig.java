package com.biluo.yygh.order.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "weixin")
public class WeixinConfig {
    private String appid;

    private String partner;

    private String partnerkey;

    private String cert;
}

