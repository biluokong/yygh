package com.biluo.yygh.sms.controller;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsApiController {
	private final SmsService smsService;
	private final StringRedisTemplate redisTemplate;

	// 发送手机验证码
	@GetMapping("send/{phone}")
	public Result sendCode(@PathVariable String phone) {
		// 生成验证码
		String code = RandomUtils.nextInt(100000, 999999) + "";
		// 调用service方法，通过整合短信服务进行发送
		boolean isSend = smsService.send(phone, code);
		// 生成验证码放到redis里面，设置有效时间
		if (isSend) {
			redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.MINUTES);
			return Result.ok();
		} else {
			return Result.fail().message("发送短信失败");
		}
	}
}
