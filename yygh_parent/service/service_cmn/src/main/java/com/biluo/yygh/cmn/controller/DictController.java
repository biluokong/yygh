package com.biluo.yygh.cmn.controller;

import com.biluo.yygh.cmn.service.DictService;
import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@RequiredArgsConstructor
public class DictController {
	private final DictService dictService;

	/**
	 * 根据数据id查询子数据列表
	 */
	@ApiOperation("根据数据id查询子数据列表")
	@GetMapping("findChildData/{id}")
	public Result<List<Dict>> findChildData(@PathVariable Long id) {
		return Result.ok(dictService.findChildData(id));
	}

	/**
	 * 导出数据字典为Excel
	 */
	@ApiOperation("导出数据字典为Excel")
	@GetMapping("exportData")
	public void exportData(HttpServletResponse response) {
		dictService.exportData(response);
	}

	/**
	 * 导入数据字典
	 */
	@ApiOperation("导入数据字典")
	@PostMapping("importData")
	public Result importData(MultipartFile file) {
		dictService.importData(file);
		return Result.ok();
	}

	//根据dictCode获取下级节点
	@ApiOperation(value = "根据dictCode获取下级节点")
	@GetMapping("findByDictCode/{dictCode}")
	public Result<List<Dict>> findByDictCode(@PathVariable String dictCode) {
		Dict parent = dictService.lambdaQuery().eq(Dict::getDictCode, dictCode).one();
		List<Dict> list = dictService.findChildData(parent.getId());
		return Result.ok(list);
	}

	////////////////////////////////////// Feign /////////////////////////////////////

	/**
	 * 根据dictCode和value集合获取数据字典集合
	 */
	@GetMapping("list")
	public Result<List<Dict>> getListByDictCodes(@RequestParam(required = false) String dictCode,
												 @RequestParam List<String> values) {
		if (StringUtils.isEmpty(dictCode)) {
			List<Dict> dictList = dictService.lambdaQuery()
					.in(Dict::getValue, values)
					.list();
			return Result.ok(dictList);
		}
		Dict dictParent = dictService.lambdaQuery().eq(Dict::getDictCode, dictCode).one();
		List<Dict> dictList = dictService.lambdaQuery()
				.eq(Dict::getParentId, dictParent.getId())
				.in(Dict::getValue, values)
				.list();
		return Result.ok(dictList);
	}
}
