package com.online.edu.infrastructure.apigateway.filter;

import com.google.gson.JsonObject;
import com.online.edu.common.base.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 创建一个匹配路由的对象
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        // 匹配路由是否带auth ，验证请求中是否又token，是否已登陆
        if(antPathMatcher.match("/api/**/auth/**", path)) {
            // 请求头中获取token，注意请求头中获取到的数据 都是放在list集合 中的
            List<String> tokenList = request.getHeaders().get("token");
            //没有token
            if(null == tokenList) {
                // 获取响应
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
            }

            // 获取到了token 进行校验 判断校验是否成功
            Boolean isCheck = JwtUtils.checkJwtTToken(tokenList.get(0));
            if(!isCheck) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
            }
        }
        //放行
        return chain.filter(exchange);
    }

    //定义当前过滤器的优先级，值越小，优先级越高
    @Override
    public int getOrder() {
        return 0;
    }


    // 使用webflux输入请求信息
    private Mono<Void> out(ServerHttpResponse response) {

        // 定义自定义的返回结果：鉴定失败
        JsonObject message = new JsonObject();
        message.addProperty("success", false);
        message.addProperty("code", 28004);
        message.addProperty("data", "");
        message.addProperty("message", "鉴权失败");
        // 转换成字节数组
        byte[] bytes = message.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        //输出http响应
        return response.writeWith(Mono.just(buffer));
    }
}
