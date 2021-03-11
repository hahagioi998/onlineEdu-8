package com.online.edu.service.sms.service;

import com.aliyuncs.exceptions.ClientException;

public interface SmsService {

    // 发送短信 mobile：手机号  checkCode：验证码
    void send(String mobile, String checkCode) throws ClientException;

}
