package com.biluo.yygh.hosp.controller;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.hosp.service.HospitalService;
import com.biluo.yygh.model.hosp.Hospital;
import com.biluo.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
@RequiredArgsConstructor
public class HospitalController {
	private final HospitalService hospitalService;

	// 医院列表(条件查询分页)
	@GetMapping("list/{page}/{limit}")
	public Result<Page<Hospital>> listHosp(@PathVariable Integer page,
										   @PathVariable Integer limit,
										   HospitalQueryVo hospitalQueryVo) {
		Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);

		return Result.ok(pageModel);
	}

	// 更新医院上线状态
	@ApiOperation(value = "更新医院上线状态")
	@GetMapping("updateHospStatus/{id}/{status}")
	public Result updateHospStatus(@PathVariable String id, @PathVariable Integer status) {
		hospitalService.updateStatus(id, status);
		return Result.ok();
	}

	// 医院详情信息
	@ApiOperation(value = "医院详情信息")
	@GetMapping("showHospDetail/{id}")
	public Result showHospDetail(@PathVariable String id) {
		Map<String, Object> map = hospitalService.getHospById(id);
		return Result.ok(map);
	}
}
