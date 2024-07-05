package com.biluo.yygh.hosp.receiver;

import com.biluo.common.rabbit.constant.MqConst;
import com.biluo.common.rabbit.service.RabbitService;
import com.biluo.yygh.hosp.service.ScheduleService;
import com.biluo.yygh.model.hosp.Schedule;
import com.biluo.yygh.vo.order.OrderMqVo;
import com.biluo.yygh.vo.sms.MsmVo;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class HospitalReceiver {
	private final ScheduleService scheduleService;
	private final RabbitService rabbitService;

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
			exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
			key = {MqConst.ROUTING_ORDER}
	))
	public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
		Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
		if (null != orderMqVo.getAvailableNumber()) {
			// 下单成功更新预约数
			schedule.setReservedNumber(orderMqVo.getReservedNumber());
			schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
		} else {
			// 取消预约更新预约数
			int availableNumber = schedule.getAvailableNumber().intValue() + 1;
			schedule.setAvailableNumber(availableNumber);
		}
		scheduleService.update(schedule);

		// 发送短信
		MsmVo msmVo = orderMqVo.getMsmVo();
		if (null != msmVo) {
			rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
		}
	}

}
