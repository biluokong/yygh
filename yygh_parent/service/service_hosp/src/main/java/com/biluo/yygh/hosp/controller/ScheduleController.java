package com.biluo.yygh.hosp.controller;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.hosp.service.ScheduleService;
import com.biluo.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
@RequiredArgsConstructor
public class ScheduleController {
	private final ScheduleService scheduleService;

	// 根据医院编号 和 科室编号 ，查询排班规则数据
	@ApiOperation(value = "查询排班规则数据")
	@GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
	public Result<Map<String, Object>> getScheduleRule(@PathVariable long page,
													   @PathVariable long limit,
													   @PathVariable String hoscode,
													   @PathVariable String depcode) {
		Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
		return Result.ok(map);
	}

	// 根据医院编号 、科室编号和工作日期，查询排班详细信息
	@ApiOperation(value = "查询排班详细信息")
	@GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
	public Result<List<Schedule>> getScheduleDetail(@PathVariable String hoscode,
													@PathVariable String depcode,
													@PathVariable String workDate) {
		List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
		return Result.ok(list);
	}
}
