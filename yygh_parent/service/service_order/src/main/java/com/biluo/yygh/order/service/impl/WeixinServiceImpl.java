package com.biluo.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biluo.yygh.enums.PaymentTypeEnum;
import com.biluo.yygh.enums.RefundStatusEnum;
import com.biluo.yygh.model.order.OrderInfo;
import com.biluo.yygh.model.order.PaymentInfo;
import com.biluo.yygh.model.order.RefundInfo;
import com.biluo.yygh.order.service.OrderService;
import com.biluo.yygh.order.service.PaymentService;
import com.biluo.yygh.order.service.RefundInfoService;
import com.biluo.yygh.order.service.WeixinService;
import com.biluo.yygh.order.utils.HttpClient;
import com.biluo.yygh.order.utils.WeixinConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WeixinServiceImpl implements WeixinService {
	private final WeixinConfig weixinConfig;
	private final RedisTemplate redisTemplate;
	private final OrderService orderService;
	private final PaymentService paymentService;
	private final RefundInfoService refundInfoService;

	// 生成微信支付二维码
	@Override
	public Map createNative(Long orderId) {
		try {
			// 从redis获取数据
			Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
			if (payMap != null) {
				return payMap;
			}
			// 1 根据orderId获取订单信息
			OrderInfo order = orderService.getById(orderId);
			// 2 向支付记录表添加信息
			paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
			// 3设置参数，
			// 把参数转换xml格式，使用商户key进行加密
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("appid", weixinConfig.getAppid());
			paramMap.put("mch_id", weixinConfig.getPartner());
			paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
			String body = order.getReserveDate() + "就诊" + order.getDepname();
			paramMap.put("body", body);
			paramMap.put("out_trade_no", order.getOutTradeNo());
			// paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
			paramMap.put("total_fee", "1"); // 为了测试，统一写成这个值
			paramMap.put("spbill_create_ip", "127.0.0.1");
			paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
			paramMap.put("trade_type", "NATIVE");
			// 4 调用微信生成二维码接口,httpclient调用
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			// 设置map参数
			client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weixinConfig.getPartnerkey()));
			client.setHttps(true);
			client.post(weixinConfig.getCert());
			// 5 返回相关数据
			String xml = client.getContent();
			// 转换map集合
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
			System.out.println("resultMap:" + resultMap);
			// 6 封装返回结果集
			Map<String, Object> map = new HashMap<>();
			map.put("orderId", orderId);
			map.put("totalFee", order.getAmount());
			map.put("resultCode", resultMap.get("result_code"));
			map.put("codeUrl", resultMap.get("code_url")); // 二维码地址

			if (resultMap.get("result_code") != null) {
				redisTemplate.opsForValue().set(orderId.toString(), map, 120, TimeUnit.MINUTES);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 调用微信接口实现支付状态查询
	@Override
	public Map<String, String> queryPayStatus(Long orderId) {
		try {
			// 1 根据orderId获取订单信息
			OrderInfo orderInfo = orderService.getById(orderId);

			// 2 封装提交参数
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("appid", weixinConfig.getAppid());
			paramMap.put("mch_id", weixinConfig.getPartner());
			paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
			paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

			// 3 设置请求内容
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weixinConfig.getPartnerkey()));
			client.setHttps(true);
			client.post(weixinConfig.getCert());

			// 4 得到微信接口返回数据
			String xml = client.getContent();
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
			System.out.println("支付状态resultMap:" + resultMap);
			// 5 把接口数据返回
			return resultMap;
		} catch (Exception e) {
			return null;
		}
	}

	// 微信退款
	@Override
	public Boolean refund(Long orderId) {
		try {
			// 获取支付记录信息
			PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
			// 添加信息到退款记录表
			RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
			// 判断当前订单数据是否已经退款
			if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
				return true;
			}
			// 调用微信接口实现退款
			// 封装需要参数
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("appid", weixinConfig.getAppid());       // 公众账号ID
			paramMap.put("mch_id", weixinConfig.getPartner());   // 商户编号
			paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
			paramMap.put("transaction_id", paymentInfo.getTradeNo()); // 微信订单号
			paramMap.put("out_trade_no", paymentInfo.getOutTradeNo()); // 商户订单编号
			paramMap.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo()); // 商户退款单号
			paramMap.put("total_fee", "1");
			paramMap.put("refund_fee", "1");
			String paramXml = WXPayUtil.generateSignedXml(paramMap, weixinConfig.getPartnerkey());
			// 设置调用接口内容
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
			client.setXmlParam(paramXml);
			client.setHttps(true);
			// 设置证书信息
			client.setCert(true);
			client.setCertPassword(weixinConfig.getPartner());
			client.post(weixinConfig.getCert());

			// 接收返回数据
			String xml = client.getContent();
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
			if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
				refundInfo.setCallbackTime(new Date());
				refundInfo.setTradeNo(resultMap.get("refund_id"));
				refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
				refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
				refundInfoService.updateById(refundInfo);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
