package com.online.edu.service.sms.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
// 需要别的地方微服务公共引用的  要加上这个@Component 让该对象呗容器管理 可共同使用
@Component
//注意prefix要写到最后一个 "." 符号之前 会自动为下面属性赋值
@ConfigurationProperties(prefix="aliyun.sms")
public class SmsProperties {
    private String regionId;
    private String keyId;
    private String keySecret;
    private String templateCode;
    private String signName;
}