package com.online.edu.service.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession   // 开启将session存入redis中
public class HttpSessionConfig {

    // 将session数据全部同步存入redis中
    //可选配置  cookie序列化
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        // session中存入信息后，服务器会返回给cookie一个sessionID：JSESSIONID。 当把session中的信息存入redis中后。cookie中会有一个名叫session的cookie与redis的key相关联
        //我们可以将Spring Session默认的Cookie Key从SESSION替换为原生的JSESSIONID
        serializer.setCookieName("JSESSIONID");
        // CookiePath设置为根路径
        serializer.setCookiePath("/");
        // 配置了相关的正则表达式，可以达到同父域下的单点登录的效果  设置cookie在顶级域名下
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        return serializer;
    }
}
