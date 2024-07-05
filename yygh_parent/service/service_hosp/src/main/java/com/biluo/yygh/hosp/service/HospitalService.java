package com.biluo.yygh.hosp.service;

import com.biluo.yygh.model.hosp.Hospital;
import com.biluo.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
	void save(Map<String, Object> paramMap);

	Hospital getByHoscode(String hoscode);

	Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

	void updateStatus(String id, Integer status);

	Map<String, Object> getHospById(String id);

	List<Hospital> findByHosname(String hosname);

	Map<String, Object> item(String hoscode);
}
