package com.biluo.yygh.sms.service;


import com.biluo.yygh.vo.sms.MsmVo;

public interface SmsService {

    //发送手机验证码
    boolean send(String phone, String code);

    //mq使用发送短信
    boolean send(MsmVo msmVo);
}
