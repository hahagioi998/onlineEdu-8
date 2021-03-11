package com.online.edu.service.trade.service;

import java.util.Map;

public interface WeixinPayService {

    /**
     *
     * @param orderNo  订单号
     * @param remoteAddr 终端支付的IP地址
     * @return
     */
    // 生成
    Map<String, Object> createNative(String orderNo, String remoteAddr);

}
