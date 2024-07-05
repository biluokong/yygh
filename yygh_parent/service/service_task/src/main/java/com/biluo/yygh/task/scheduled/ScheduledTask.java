package com.biluo.yygh.task.scheduled;

import com.biluo.common.rabbit.constant.MqConst;
import com.biluo.common.rabbit.service.RabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTask {
	private final RabbitService rabbitService;

	// 每天8点执行方法，就医提醒
	// cron表达式，设置执行间隔
	// 0 0 8 * * ?
	@Scheduled(cron = "0/30 * * * * ?")
	public void taskPatient() {
		rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
	}
}
