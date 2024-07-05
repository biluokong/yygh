package com.biluo.yygh.hosp.api;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.hosp.service.DepartmentService;
import com.biluo.yygh.hosp.service.HospitalService;
import com.biluo.yygh.hosp.service.HospitalSetService;
import com.biluo.yygh.hosp.service.ScheduleService;
import com.biluo.yygh.model.hosp.Hospital;
import com.biluo.yygh.model.hosp.Schedule;
import com.biluo.yygh.vo.hosp.DepartmentVo;
import com.biluo.yygh.vo.hosp.HospitalQueryVo;
import com.biluo.yygh.vo.hosp.ScheduleOrderVo;
import com.biluo.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
@RequiredArgsConstructor
public class HospApiController {
	private final HospitalService hospitalService;
	private final DepartmentService departmentService;
	private final ScheduleService scheduleService;
	private final HospitalSetService hospitalSetService;

	@ApiOperation(value = "查询医院列表")
	@GetMapping("findHospList/{page}/{limit}")
	public Result<Page<Hospital>> findHospList(@PathVariable Integer page,
											   @PathVariable Integer limit,
											   HospitalQueryVo hospitalQueryVo) {
		Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
		return Result.ok(hospitals);
	}

	@ApiOperation(value = "根据医院名称查询")
	@GetMapping("findByHosName/{hosname}")
	public Result<List<Hospital>> findByHosName(@PathVariable String hosname) {
		List<Hospital> list = hospitalService.findByHosname(hosname);
		return Result.ok(list);
	}

	@ApiOperation(value = "根据医院编号获取科室")
	@GetMapping("department/{hoscode}")
	public Result<List<DepartmentVo>> index(@PathVariable String hoscode) {
		List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
		return Result.ok(list);
	}

	@ApiOperation(value = "根据医院编号获取医院预约挂号详情")
	@GetMapping("findHospDetail/{hoscode}")
	public Result<Map<String, Object>> item(@PathVariable String hoscode) {
		Map<String, Object> map = hospitalService.item(hoscode);
		return Result.ok(map);
	}

	@ApiOperation(value = "获取可预约排班数据")
	@GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
	public Result<Map<String, Object>> getBookingSchedule(
			@ApiParam(name = "page", value = "当前页码", required = true)
			@PathVariable Integer page,
			@ApiParam(name = "limit", value = "每页记录数", required = true)
			@PathVariable Integer limit,
			@ApiParam(name = "hoscode", value = "医院code", required = true)
			@PathVariable String hoscode,
			@ApiParam(name = "depcode", value = "科室code", required = true)
			@PathVariable String depcode) {
		return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
	}

	@ApiOperation(value = "获取排班数据")
	@GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
	public Result<List<Schedule>> findScheduleList(
			@ApiParam(name = "hoscode", value = "医院code", required = true)
			@PathVariable String hoscode,
			@ApiParam(name = "depcode", value = "科室code", required = true)
			@PathVariable String depcode,
			@ApiParam(name = "workDate", value = "排班日期", required = true)
			@PathVariable String workDate) {
		return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
	}

	@ApiOperation(value = "获取排班id获取排班数据")
	@GetMapping("getSchedule/{scheduleId}")
	public Result<Schedule> getSchedule(@PathVariable String scheduleId) {
		Schedule schedule = scheduleService.getScheduleId(scheduleId);
		return Result.ok(schedule);
	}

	////////////////////////////////////////// Feign ////////////////////////////////////////

	@ApiOperation(value = "根据排班id获取预约下单数据")
	@GetMapping("inner/getScheduleOrderVo/{scheduleId}")
	public ScheduleOrderVo getScheduleOrderVo(
			@ApiParam(name = "scheduleId", value = "排班id", required = true)
			@PathVariable("scheduleId") String scheduleId) {
		return scheduleService.getScheduleOrderVo(scheduleId);
	}

	@ApiOperation(value = "获取医院签名信息")
	@GetMapping("inner/getSignInfoVo/{hoscode}")
	public SignInfoVo getSignInfoVo(
	        @ApiParam(name = "hoscode", value = "医院code", required = true)
	        @PathVariable("hoscode") String hoscode) {
	    return hospitalSetService.getSignInfoVo(hoscode);
	}

}
