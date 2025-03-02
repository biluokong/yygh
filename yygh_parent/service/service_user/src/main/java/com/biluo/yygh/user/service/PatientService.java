package com.biluo.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.biluo.yygh.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    //获取就诊人列表
    List<Patient> findAllUserId(Long userId);

    Patient getPatientId(Long id);
}
