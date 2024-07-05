package com.biluo.yygh.cmn.client;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.model.cmn.Dict;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "service-cmn")
@Repository
public interface DictFeignClient {

	// 根据dictCode和value集合获取数据字典集合
	@GetMapping("/admin/cmn/dict/list")
	Result<List<Dict>> getListByDictCodes(@RequestParam(required = false) String dictCode,
										  @RequestParam List<String> values);
}
