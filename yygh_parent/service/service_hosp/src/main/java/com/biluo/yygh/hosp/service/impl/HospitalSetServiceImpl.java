package com.biluo.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.yygh.common.exception.YyghException;
import com.biluo.yygh.common.result.ResultCodeEnum;
import com.biluo.yygh.hosp.mapper.HospitalSetMapper;
import com.biluo.yygh.hosp.service.HospitalSetService;
import com.biluo.yygh.model.hosp.HospitalSet;
import com.biluo.yygh.vo.hosp.HospitalSetQueryVo;
import com.biluo.yygh.vo.order.SignInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

	// 2 根据传递过来医院编码，查询数据库，查询签名
	@Override
	public String getSignKey(String hoscode) {
		HospitalSet hospitalSet = lambdaQuery().eq(HospitalSet::getHoscode, hoscode).one();
		if (hospitalSet == null) {
			throw new YyghException(ResultCodeEnum.PARAM_ERROR);
		}
		return hospitalSet.getSignKey();
	}

	@Override
	public SignInfoVo getSignInfoVo(String hoscode) {
		HospitalSet hospitalSet = lambdaQuery().eq(HospitalSet::getHoscode, hoscode).one();
		if (null == hospitalSet) {
			throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
		}
		SignInfoVo signInfoVo = new SignInfoVo();
		signInfoVo.setApiUrl(hospitalSet.getApiUrl());
		signInfoVo.setSignKey(hospitalSet.getSignKey());
		return signInfoVo;
	}

	@Override
	public IPage<HospitalSet> findPage(Long current, Long limit, HospitalSetQueryVo hospitalSetQueryVo) {
		Page<HospitalSet> page = new Page<>(current, limit);
		if (hospitalSetQueryVo == null) {
			return page(page);
		}
		String hosname = hospitalSetQueryVo.getHosname();
		String hoscode = hospitalSetQueryVo.getHoscode();
		return lambdaQuery()
				.eq(StringUtils.isNotBlank(hoscode), HospitalSet::getHoscode, hoscode)
				.like(StringUtils.isNotBlank(hosname), HospitalSet::getHosname, hosname)
				.page(page);
	}
}
