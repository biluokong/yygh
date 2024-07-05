package com.biluo.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.yygh.common.helper.HttpRequestHelper;
import com.biluo.yygh.enums.OrderStatusEnum;
import com.biluo.yygh.enums.PaymentStatusEnum;
import com.biluo.yygh.enums.PaymentTypeEnum;
import com.biluo.yygh.hosp.client.HospitalFeignClient;
import com.biluo.yygh.model.order.OrderInfo;
import com.biluo.yygh.model.order.PaymentInfo;
import com.biluo.yygh.order.mapper.PaymentMapper;
import com.biluo.yygh.order.service.OrderService;
import com.biluo.yygh.order.service.PaymentService;
import com.biluo.yygh.vo.order.SignInfoVo;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {
	private final OrderService orderService;
	private final HospitalFeignClient hospitalFeignClient;

	// 向支付记录表添加信息
	@Override
	public void savePaymentInfo(OrderInfo order, Integer paymentType) {
		// 根据订单id和支付类型，查询支付记录表是否存在相同订单
		Integer count = lambdaQuery()
				.eq(PaymentInfo::getOrderId, order.getId())
				.eq(PaymentInfo::getPaymentType, paymentType)
				.count();
		if (count > 0) {
			return;
		}
		// 添加记录
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setCreateTime(new Date());
		paymentInfo.setOrderId(order.getId());
		paymentInfo.setPaymentType(paymentType);
		paymentInfo.setOutTradeNo(order.getOutTradeNo());
		paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
		String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd") + "|" + order.getHosname() + "|" + order.getDepname() + "|" + order.getTitle();
		paymentInfo.setSubject(subject);
		paymentInfo.setTotalAmount(order.getAmount());
		baseMapper.insert(paymentInfo);
	}

	// 更新订单状态
	@Override
	public void paySuccess(String out_trade_no, Map<String, String> resultMap) {
		// 1 根据订单编号得到支付记录
		PaymentInfo paymentInfo = lambdaQuery()
				.eq(PaymentInfo::getOutTradeNo, out_trade_no)
				.eq(PaymentInfo::getPaymentType, PaymentTypeEnum.WEIXIN.getStatus())
				.one();

		// 2 更新支付记录信息
		paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
		paymentInfo.setCallbackTime(new Date());
		paymentInfo.setTradeNo(resultMap.get("transaction_id"));
		paymentInfo.setCallbackContent(resultMap.toString());
		baseMapper.updateById(paymentInfo);

		// 3 根据订单号得到订单信息
		// 4 更新订单信息
		OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
		orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
		orderService.updateById(orderInfo);

		// 5 调用医院接口，更新订单支付信息
		SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("hoscode", orderInfo.getHoscode());
		reqMap.put("hosRecordId", orderInfo.getHosRecordId());
		reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
		String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
		reqMap.put("sign", sign);
		JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
	}

	/**
	 * 获取支付记录
	 *
	 * @param orderId
	 * @param paymentType
	 * @return
	 */
	@Override
	public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
		return lambdaQuery()
				.eq(PaymentInfo::getOrderId, orderId)
				.eq(PaymentInfo::getPaymentType, paymentType)
				.one();
	}
}
