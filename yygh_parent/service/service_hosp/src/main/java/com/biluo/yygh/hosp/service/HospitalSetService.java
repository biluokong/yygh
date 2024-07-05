package com.biluo.yygh.hosp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.biluo.yygh.model.hosp.HospitalSet;
import com.biluo.yygh.vo.hosp.HospitalSetQueryVo;
import com.biluo.yygh.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
	IPage<HospitalSet> findPage(Long current, Long limit, HospitalSetQueryVo hospitalSetQueryVo);

	String getSignKey(String hoscode);

	SignInfoVo getSignInfoVo(String hoscode);
}
