package com.biluo.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.yygh.enums.RefundStatusEnum;
import com.biluo.yygh.model.order.PaymentInfo;
import com.biluo.yygh.model.order.RefundInfo;
import com.biluo.yygh.order.mapper.RefundInfoMapper;
import com.biluo.yygh.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
	// 保存退款记录
	@Override
	public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
		// 判断是否有重复数据添加
		QueryWrapper<RefundInfo> wrapper = new QueryWrapper<>();
		wrapper.eq("order_id", paymentInfo.getOrderId());
		wrapper.eq("payment_type", paymentInfo.getPaymentType());
		RefundInfo refundInfo = baseMapper.selectOne(wrapper);
		if (refundInfo != null) {// 有相同数据
			return refundInfo;
		}
		// 添加记录
		refundInfo = new RefundInfo();
		refundInfo.setCreateTime(new Date());
		refundInfo.setOrderId(paymentInfo.getOrderId());
		refundInfo.setPaymentType(paymentInfo.getPaymentType());
		refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
		refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
		refundInfo.setSubject(paymentInfo.getSubject());
		refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
		baseMapper.insert(refundInfo);
		return refundInfo;
	}
}
