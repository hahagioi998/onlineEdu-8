package com.online.edu.service.oss.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


//加上Component注解后，应用程序启动后会自动的把这个对象初始化出来
@Component
@Data
@ConfigurationProperties(prefix ="aliyun.oss")
public class OssProperties {

    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;

}
