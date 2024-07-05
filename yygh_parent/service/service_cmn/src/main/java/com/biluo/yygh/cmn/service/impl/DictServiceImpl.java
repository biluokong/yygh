package com.biluo.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.yygh.cmn.litener.DictListener;
import com.biluo.yygh.cmn.mapper.DictMapper;
import com.biluo.yygh.cmn.service.DictService;
import com.biluo.yygh.model.cmn.Dict;
import com.biluo.yygh.vo.cmn.DictEeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
	@Override
	@Cacheable(value = "dict", keyGenerator = "keyGenerator")
	public List<Dict> findChildData(Long id) {
		List<Dict> dictList = lambdaQuery()
				.eq(Dict::getParentId, id)
				.list();
		List<Integer> countList = baseMapper.selectChildCountsByParentId(id);
		for (int i = 0; i < dictList.size(); i++) {
			dictList.get(i).setHasChildren(countList.get(i) > 0);
		}
		return dictList;
	}

	@Override
	public void exportData(HttpServletResponse response) {
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf-8");
		String fileName = "dict";
		response.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");
		List<Dict> dictList = this.list();
		ArrayList<DictEeVo> dictVoList = new ArrayList<>();
		for (Dict dict : dictList) {
			DictEeVo dictEeVo = new DictEeVo();
			BeanUtils.copyProperties(dict, dictEeVo);
			dictVoList.add(dictEeVo);
		}
		try {
			EasyExcel.write(response.getOutputStream(), DictEeVo.class)
					.sheet("dict")
					.doWrite(dictVoList);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@CacheEvict(value = "dict", allEntries = true)
	public void importData(MultipartFile file) {
		try {
			EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(this))
					.doReadAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
