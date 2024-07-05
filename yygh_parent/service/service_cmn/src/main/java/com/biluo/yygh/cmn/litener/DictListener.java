package com.biluo.yygh.cmn.litener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.biluo.yygh.cmn.mapper.DictMapper;
import com.biluo.yygh.cmn.service.DictService;
import com.biluo.yygh.model.cmn.Dict;
import com.biluo.yygh.vo.cmn.DictEeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DictListener extends AnalysisEventListener<DictEeVo> {
	private final DictService dictService;
	private final List<Dict> cacheList = new ArrayList<>();
	public static final int BATCH_COUNT = 1000;

	@Override
	public void invoke(DictEeVo dictEeVo, AnalysisContext context) {
		Dict dict = new Dict();
		BeanUtils.copyProperties(dictEeVo, dict);
		if (cacheList.size() < BATCH_COUNT) {
			cacheList.add(dict);
		} else {
			dictService.saveBatch(cacheList);
			cacheList.clear();
		}
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		if (cacheList.size() > 0) {
			dictService.saveBatch(cacheList);
			cacheList.clear();
		}
	}
}
