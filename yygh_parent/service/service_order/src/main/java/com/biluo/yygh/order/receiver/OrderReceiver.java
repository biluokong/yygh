package com.biluo.yygh.order.receiver;

import com.biluo.common.rabbit.constant.MqConst;
import com.biluo.yygh.order.service.OrderService;
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
public class OrderReceiver {
	private final OrderService orderService;

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = MqConst.QUEUE_TASK_8, durable = "true"),
			exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
			key = {MqConst.ROUTING_TASK_8}
	))
	public void patientTips(Message message, Channel channel) throws IOException {
		orderService.patientTips();
	}

}
