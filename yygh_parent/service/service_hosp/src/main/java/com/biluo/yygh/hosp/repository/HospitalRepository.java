package com.biluo.yygh.hosp.repository;

import com.biluo.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
	//判断是否存在数据
	Hospital getHospitalByHoscode(String hoscode);

	List<Hospital> findHospitalByHosnameLike(String hosname);
}
