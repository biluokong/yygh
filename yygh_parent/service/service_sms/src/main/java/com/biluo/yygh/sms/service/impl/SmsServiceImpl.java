package com.biluo.yygh.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.biluo.yygh.sms.config.AliyunSmsConfig;
import com.biluo.yygh.sms.service.SmsService;
import com.biluo.yygh.vo.sms.MsmVo;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {
	private final AliyunSmsConfig aliyunSmsConfig;

	@Override
	public boolean send(String phone, String code) {
		if (!StringUtils.hasText(phone)) {
			return false;
		}
		Config config = new Config()
				.setAccessKeyId(aliyunSmsConfig.getAk())
				.setAccessKeySecret(aliyunSmsConfig.getSk());
		config.endpoint = "dysmsapi.aliyuncs.com";
		try {
			Client client = new Client(config);
			String templateParam = "{\"code\":\"" + code + "\"}";
			SendSmsRequest request = new SendSmsRequest()
					.setPhoneNumbers(phone)
					.setSignName(aliyunSmsConfig.getSignName())
					.setTemplateCode(aliyunSmsConfig.getTemplateCode())
					.setTemplateParam(templateParam);
			client.sendSmsWithOptions(request, new RuntimeOptions());
			return true;
		} catch (Exception e) {
			log.error("发送短信失败");
			return false;
		}
	}

	//mq发送短信封装
	@Override
	public boolean send(MsmVo msmVo) {
		if(!StringUtils.isEmpty(msmVo.getPhone())) {
			return this.send(msmVo.getPhone(), msmVo.getParam());
		}
		return false;
	}

	private boolean send(String phone, Map<String, Object> param) {
		if (!StringUtils.hasText(phone)) {
			return false;
		}
		Config config = new Config()
				.setAccessKeyId(aliyunSmsConfig.getAk())
				.setAccessKeySecret(aliyunSmsConfig.getSk());
		config.endpoint = "dysmsapi.aliyuncs.com";
		try {
			Client client = new Client(config);
			String templateParam = "{\"code\":\"" + 1111 + "\"}";
			// String templateParam = JSONObject.toJSONString(param);
			SendSmsRequest request = new SendSmsRequest()
					.setPhoneNumbers(phone)
					.setSignName(aliyunSmsConfig.getSignName())
					.setTemplateCode(aliyunSmsConfig.getTemplateCode())
					.setTemplateParam(templateParam);
			client.sendSmsWithOptions(request, new RuntimeOptions());
			return true;
		} catch (Exception e) {
			log.error("发送短信失败");
			return false;
		}
	}
}
