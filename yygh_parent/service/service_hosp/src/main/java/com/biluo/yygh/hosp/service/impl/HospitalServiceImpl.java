package com.biluo.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biluo.yygh.cmn.client.DictFeignClient;
import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.hosp.repository.HospitalRepository;
import com.biluo.yygh.hosp.service.HospitalService;
import com.biluo.yygh.model.cmn.Dict;
import com.biluo.yygh.model.hosp.Hospital;
import com.biluo.yygh.vo.hosp.HospitalQueryVo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
	private final HospitalRepository hospitalRepository;
	private final DictFeignClient dictFeignClient;

	@Override
	public void save(Map<String, Object> paramMap) {
		// 把参数map集合转换对象 Hospital
		String mapString = JSONObject.toJSONString(paramMap);
		Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

		// 判断是否存在数据
		String hoscode = hospital.getHoscode();
		Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

		// 如果存在，进行修改
		if (hospitalExist != null) {
			hospital.setStatus(hospitalExist.getStatus());
			hospital.setCreateTime(hospitalExist.getCreateTime());
		} else {// 如果不存在，进行添加
			hospital.setStatus(0);
			hospital.setCreateTime(new Date());
		}
		hospital.setUpdateTime(new Date());
		hospital.setIsDeleted(0);
		hospitalRepository.save(hospital);
	}

	@Override
	public Hospital getByHoscode(String hoscode) {
		return hospitalRepository.getHospitalByHoscode(hoscode);
	}

	// 医院列表(条件查询分页)
	@Override
	public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
		// 创建pageable对象
		Pageable pageable = PageRequest.of(page - 1, limit);
		// 创建条件匹配器
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				.withIgnoreCase(true);
		// hospitalSetQueryVo转换Hospital对象
		Hospital hospital = new Hospital();
		BeanUtils.copyProperties(hospitalQueryVo, hospital);
		// 创建对象
		Example<Hospital> example = Example.of(hospital, matcher);
		// 调用方法实现查询
		Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

		// 获取查询list集合，对每个数据进行医院等级封装
		List<Hospital> hospList = pages.getContent();
		// 1.获取医院等级的字典values和医院地址的字典values集合
		List<String> hospTypeValues = new ArrayList<>();
		List<String> hospAddrValues = new ArrayList<>();
		hospList.forEach(hosp -> {
			hospTypeValues.add(hosp.getHostype());
			hospAddrValues.add(hosp.getProvinceCode());
			hospAddrValues.add(hosp.getCityCode());
			hospAddrValues.add(hosp.getDistrictCode());
		});
		List<Dict> hospDictList = dictFeignClient.getListByDictCodes("Hostype", hospTypeValues).getData();
		List<Dict> AddrDictList = dictFeignClient.getListByDictCodes(null, hospAddrValues).getData();
		// TODO Feign熔短处理和远程调用失败处理
		// 2.遍历list集合，封装医院等级和医院地址
		hospList.forEach(hosp -> {
			String hostypeString = hospDictList.stream()
					.filter(dict -> dict.getValue().equals(hosp.getHostype()))
					.map(Dict::getName).collect(Collectors.toList()).get(0);
			String provinceString = AddrDictList.stream()
					.filter(dict -> dict.getValue().equals(hosp.getProvinceCode()))
					.map(Dict::getName).collect(Collectors.toList()).get(0);
			String cityString = AddrDictList.stream()
					.filter(dict -> dict.getValue().equals(hosp.getCityCode()))
					.map(Dict::getName).collect(Collectors.toList()).get(0);
			String districtString = AddrDictList.stream()
					.filter(dict -> dict.getValue().equals(hosp.getDistrictCode()))
					.map(Dict::getName).collect(Collectors.toList()).get(0);
			hosp.getParam().put("hostypeString", hostypeString);
			hosp.getParam().put("fullAddress", provinceString + cityString + districtString);
		});
		return pages;
	}

	// 更新医院上线状态
	@Override
	public void updateStatus(String id, Integer status) {
		// 根据id查询医院信息
		Hospital hospital = hospitalRepository.findById(id).get();
		// 设置修改的值
		hospital.setStatus(status);
		hospital.setUpdateTime(new Date());
		hospitalRepository.save(hospital);
	}

	@Override
	public Map<String, Object> getHospById(String id) {
		Map<String, Object> result = new HashMap<>();
		Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
		// 医院基本信息（包含医院等级）
		result.put("hospital", hospital);
		// 单独处理更直观
		result.put("bookingRule", hospital.getBookingRule());
		// 不需要重复返回
		hospital.setBookingRule(null);
		return result;
	}

	// 根据医院名称查询
	@Override
	public List<Hospital> findByHosname(String hosname) {
		return hospitalRepository.findHospitalByHosnameLike(hosname);
	}

	// 根据医院编号获取医院预约挂号详情
	@Override
	public Map<String, Object> item(String hoscode) {
		Map<String, Object> result = new HashMap<>();
		// 医院详情
		Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
		result.put("hospital", hospital);
		// 预约规则
		result.put("bookingRule", hospital.getBookingRule());
		// 不需要重复返回
		hospital.setBookingRule(null);
		return result;
	}


	// 获取查询list集合，遍历进行医院等级封装
	private Hospital setHospitalHosType(Hospital hospital) {
		// 根据dictCode和value获取医院等级名称
		String hostypeString = dictFeignClient.getListByDictCodes("Hostype",
				Lists.newArrayList(hospital.getHostype())).getData().get(0).getName();
		// 查询省 市  地区
		String provinceString = dictFeignClient.getListByDictCodes(null,
				Lists.newArrayList(hospital.getProvinceCode())).getData().get(0).getName();
		String cityString = dictFeignClient.getListByDictCodes(null,
				Lists.newArrayList(hospital.getProvinceCode())).getData().get(0).getName();
		String districtString = dictFeignClient.getListByDictCodes(null,
				Lists.newArrayList(hospital.getProvinceCode())).getData().get(0).getName();

		hospital.getParam().put("fullAddress", provinceString + cityString + districtString);
		hospital.getParam().put("hostypeString", hostypeString);
		return hospital;
	}
}
