package com.online.edu.service.trade.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.ExceptionUtils;
import com.online.edu.common.base.util.HttpClientUtils;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.trade.entity.Order;
import com.online.edu.service.trade.service.OrderService;
import com.online.edu.service.trade.service.WeixinPayService;
import com.online.edu.service.trade.util.WeixinPayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WeixinPayServiceImpl implements WeixinPayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayProperties weixinPayProperties;

    @Override
    public Map<String, Object> createNative(String orderNo, String remoteAddr) {
        try {
            //根据课程订单号获取订单信息
            Order order = orderService.getOrderByOrderNo(orderNo);
            //调用微信api接口：统一下单（支付订单）
            HttpClientUtils client = new HttpClientUtils("https://api.mch.weixin.qq.com/pay/unifiedorder");
            // 根据https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1 接口文档生成参数 否的 都不添加了
            // 组装url参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", weixinPayProperties.getAppId());//关联的公众号的appid
            params.put("mch_id", weixinPayProperties.getPartner());//商户号
            params.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
            params.put("body", order.getCourseTitle());
            params.put("out_trade_no", orderNo);
            //注意，这里必须使用字符串类型的参数（总金额：分）
            //这里转换为字符串类型 order.getTotalFee()为BigDecimal类型 +.intValue()为int ,+""为string
            String totalFee = order.getTotalFee().intValue() + "";
            params.put("total_fee", totalFee); // 订单的总金额 ：课程价格
            params.put("spbill_create_ip", remoteAddr); //支付终端IP
            params.put("notify_url", weixinPayProperties.getNotifyUrl()); //通知地址： 接口回调
            params.put("trade_type", "NATIVE"); // 交易类型 NATIVE：网页二维码

            //将参数线转换为xml的字符串 并且在字符串的最后追加计算的签名 生成签名sign
            String xmlParams = WXPayUtil.generateSignedXml(params, weixinPayProperties.getPartnerKey());
            log.info(xmlParams);

            client.setXmlParam(xmlParams);//将参数放入请求对象的方法体
            client.setHttps(true);//使用https形式发送
            client.post();//必须用post发送请求
            //得到响应结果
            String resultXml = client.getContent();
            log.info("\n resultXml：\n" + resultXml);

            // 将得到的结果xml形式的转换为map形式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            //错误处理
            if("FAIL".equals(resultMap.get("return_code")) || "FAIL".equals(resultMap.get("result_code"))){
                log.error("微信支付统一下单错误 - "
                        + "return_code: " + resultMap.get("return_code")
                        + "return_msg: " + resultMap.get("return_msg")
                        + "result_code: " + resultMap.get("result_code")
                        + "err_code: " + resultMap.get("err_code")
                        + "err_code_des: " + resultMap.get("err_code_des"));

                throw new EduException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
            }

            //组装需要的内容
            Map<String, Object> map = new HashMap<>();
            map.put("result_code", resultMap.get("result_code"));//响应码
            map.put("code_url", resultMap.get("code_url"));//生成二维码的url
            map.put("course_id", order.getCourseId());//课程id
            map.put("total_fee", order.getTotalFee());//订单总金额
            map.put("out_trade_no", orderNo);//订单号

            return map;
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new EduException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
        }

    }
}
