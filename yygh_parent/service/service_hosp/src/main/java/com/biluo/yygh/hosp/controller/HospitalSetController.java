package com.biluo.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.common.utils.MD5;
import com.biluo.yygh.hosp.service.HospitalSetService;
import com.biluo.yygh.model.hosp.HospitalSet;
import com.biluo.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@RequiredArgsConstructor
public class HospitalSetController {
	private final HospitalSetService hospitalSetService;

	/**
	 * 查询全部医院设置信息
	 */
	@ApiOperation("获取所有医院设置信息")
	@GetMapping("findAll")
	public Result<List<HospitalSet>> findAll() {
		return Result.ok(hospitalSetService.list());
	}

	/**
	 * 逻辑删除医院设置
	 */
	@ApiOperation("逻辑删除医院设置")
	@DeleteMapping("{id}")
	public Result removeById(@PathVariable Long id) {
		boolean success = hospitalSetService.removeById(id);
		return success ? Result.ok() : Result.fail();
	}

	/**
	 * 带条件的分页查询
	 */
	@ApiOperation("带条件的分页查询")
	@PostMapping("findPageHospSet/{current}/{limit}")
	public Result<IPage<HospitalSet>> findPage(@PathVariable Long current,
						   @PathVariable Long limit,
						   @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
		return Result.ok(hospitalSetService.findPage(current, limit, hospitalSetQueryVo));
	}

	/**
	 * 新增医院设置
	 */
	@ApiOperation("新增医院设置")
	@PostMapping("saveHospitalSet")
	public Result<Boolean> save(@RequestBody HospitalSet hospitalSet) {
		//设置状态 1-使用， 0-不能使用
		hospitalSet.setStatus(1L);
		//签名秘钥
		Random random = new Random();
		hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
		//调用service
		boolean success = hospitalSetService.save(hospitalSet);
		return success ? Result.ok() : Result.fail();
	}

	/**
	 * 根据id获取医院设置
	 */
	@ApiOperation("根据id获取医院设置")
	@GetMapping("getHospSet/{id}")
	public Result<HospitalSet> getById(@PathVariable Long id) {
		return Result.ok(hospitalSetService.getById(id));
	}

	/**
	 * 修改医院设置
	 */
	@ApiOperation("修改医院设置")
	@PostMapping("updateHospitalSet")
	public Result<Boolean> update(@RequestBody HospitalSet hospitalSet) {
		return hospitalSetService.updateById(hospitalSet) ? Result.ok() : Result.fail();
	}

	/**
	 * 批量删除医院设置
	 */
	@ApiOperation("批量删除医院设置")
	@DeleteMapping("batchRemove")
	public Result<Boolean> batchRemove(@RequestBody List<Long> idList) {
		return hospitalSetService.removeByIds(idList) ? Result.ok() : Result.fail();
	}

	/**
	 * 锁定和解锁医院设置
	 */
	@ApiOperation("锁定和解锁医院设置")
	@PutMapping("lockHospitalSet/{id}/{status}")
	public Result lock(@PathVariable Long id, @PathVariable Integer status) {
		//根据id查询医院设置信息
		HospitalSet hospitalSet = hospitalSetService.getById(id);
		//设置状态
		hospitalSet.setStatus(Long.valueOf(status));
		//调用方法
		return hospitalSetService.updateById(hospitalSet) ? Result.ok() : Result.fail();
	}

	/**
	 * 发送签名秘钥
	 */
	@ApiOperation("发送签名秘钥")
	@PutMapping("sendKey/{id}")
	public Result<Boolean> sendKey(@PathVariable Long id) {
		//根据id查询医院设置信息
		HospitalSet hospitalSet = hospitalSetService.getById(id);
		//获取医院名称
		String hosname = hospitalSet.getHosname();
		//获取签名秘钥
		String signKey = hospitalSet.getSignKey();
		//TODO 发送短信
		return Result.ok();
	}
}
