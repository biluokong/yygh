package com.biluo.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.yygh.cmn.client.DictFeignClient;
import com.biluo.yygh.model.cmn.Dict;
import com.biluo.yygh.model.user.Patient;
import com.biluo.yygh.user.mapper.PatientMapper;
import com.biluo.yygh.user.service.PatientService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.biluo.yygh.enums.DictEnum.CERTIFICATES_TYPE;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
	private final DictFeignClient dictFeignClient;

	// 获取就诊人列表
	@Override

	public List<Patient> findAllUserId(Long userId) {
		// 根据userid查询所有就诊人信息列表
		List<Patient> patientList = lambdaQuery().eq(Patient::getUserId, userId).list();
		// 通过远程调用，得到编码对应具体内容，查询数据字典表内容
		ArrayList<String> certificatesTypeCodes = new ArrayList<>();
		ArrayList<String> contactsCertificatesTypeCodes = new ArrayList<>();
		ArrayList<String> addressCodes = new ArrayList<>();
		patientList.forEach(patient -> {
			certificatesTypeCodes.add(patient.getCertificatesType());
			contactsCertificatesTypeCodes.add(patient.getContactsCertificatesType());
			addressCodes.add(patient.getProvinceCode());
			addressCodes.add(patient.getCityCode());
			addressCodes.add(patient.getDistrictCode());
		});
		List<Dict> certificatesTypeValues = dictFeignClient
				.getListByDictCodes(CERTIFICATES_TYPE.getDictCode(), certificatesTypeCodes).getData();
		List<Dict> contactsCertificatesTypeValues = dictFeignClient
				.getListByDictCodes(CERTIFICATES_TYPE.getDictCode(), contactsCertificatesTypeCodes).getData();
		List<Dict> addressValues = dictFeignClient.getListByDictCodes(null, addressCodes).getData();
		patientList.forEach(patient -> {
			// 根据证件类型编码，获取证件类型具体指
			String certificatesTypeString = certificatesTypeValues.stream()
					.filter(dict -> dict.getValue().equals(patient.getCertificatesType()))
					.collect(Collectors.toList()).get(0).getName();
			// 联系人证件类型
			String contactsCertificatesTypeString = "";
			if (ObjectUtils.isNotEmpty(contactsCertificatesTypeValues)) {
				contactsCertificatesTypeString = contactsCertificatesTypeValues.stream()
						.filter(dict -> dict.getValue().equals(patient.getContactsCertificatesType()))
						.collect(Collectors.toList()).get(0).getName();
			}
			// 省
			String provinceString = addressValues.stream()
					.filter(dict -> dict.getValue().equals(patient.getProvinceCode()))
					.collect(Collectors.toList()).get(0).getName();
			// 市
			String cityString = addressValues.stream()
					.filter(dict -> dict.getValue().equals(patient.getCityCode()))
					.collect(Collectors.toList()).get(0).getName();
			// 区
			String districtString = addressValues.stream()
					.filter(dict -> dict.getValue().equals(patient.getDistrictCode()))
					.collect(Collectors.toList()).get(0).getName();
			setParams(patient, certificatesTypeString, contactsCertificatesTypeString, provinceString, cityString, districtString);
		});
		return patientList;
	}

	@Override
	public Patient getPatientId(Long id) {
		return this.packPatient(baseMapper.selectById(id));
	}

	private void setParams(Patient patient, String certificatesTypeString, String contactsCertificatesTypeString, String provinceString, String cityString, String districtString) {
		patient.getParam().put("certificatesTypeString", certificatesTypeString);
		patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
		patient.getParam().put("provinceString", provinceString);
		patient.getParam().put("cityString", cityString);
		patient.getParam().put("districtString", districtString);
		patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
	}

	// Patient对象里面其他参数封装
	private Patient packPatient(Patient patient) {
		// 根据证件类型编码，获取证件类型具体指
		String certificatesTypeString =
				dictFeignClient.getListByDictCodes(CERTIFICATES_TYPE.getDictCode(),
								Lists.newArrayList(patient.getCertificatesType()))
						.getData().get(0).getName();// 联系人证件
		// 联系人证件类型
		List<Dict> dictList = dictFeignClient.getListByDictCodes(CERTIFICATES_TYPE.getDictCode(),
						Lists.newArrayList(patient.getContactsCertificatesType())).getData();
		String contactsCertificatesTypeString = "";
		if (ObjectUtils.isNotEmpty(dictList)) {
			contactsCertificatesTypeString = dictList.get(0).getName();
		}

		List<Dict> addressValues = dictFeignClient.getListByDictCodes(null, Lists.newArrayList(
				patient.getProvinceCode(), patient.getCityCode(), patient.getDistrictCode())).getData();
		// 省
		String provinceString = addressValues.stream()
				.filter(dict -> dict.getValue().equals(patient.getProvinceCode()))
				.collect(Collectors.toList()).get(0).getName();
		// 市
		String cityString = addressValues.stream()
				.filter(dict -> dict.getValue().equals(patient.getCityCode()))
				.collect(Collectors.toList()).get(0).getName();
		// 区
		String districtString = addressValues.stream()
				.filter(dict -> dict.getValue().equals(patient.getDistrictCode()))
				.collect(Collectors.toList()).get(0).getName();

		setParams(patient, certificatesTypeString, contactsCertificatesTypeString, provinceString, cityString, districtString);
		return patient;
	}
}
