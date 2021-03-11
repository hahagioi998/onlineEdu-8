package com.online.edu.service.trade.controller.api;

import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.online.edu.common.base.result.R;
import com.online.edu.common.base.util.StreamUtils;
import com.online.edu.service.trade.entity.Order;
import com.online.edu.service.trade.service.OrderService;
import com.online.edu.service.trade.service.WeixinPayService;
import com.online.edu.service.trade.util.WeixinPayProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trade/weixin-pay")
@Api(tags = "网站微信支付")
//@CrossOrigin //跨域
@Slf4j
public class ApiWeixinPayController {

    // TODO 我们需要添加支付宝支付的功能

    @Autowired
    private WeixinPayService weixinPayService;
    @Autowired
    private WeixinPayProperties weixinPayProperties;
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单二维码的url接口")
    @GetMapping("create-native/{orderNo}")
    public R createNative(@PathVariable String orderNo, HttpServletRequest request) {
        // 获取请求的IP地址
        String remoteAddr = request.getRemoteAddr();
        Map map = weixinPayService.createNative(orderNo, remoteAddr);
        return R.ok().data(map);
    }

    /**
     * 支付回调：注意这里是【post】方式
     * @param request  接受请求消息
     * @param response 对微信的应答
     * @return
     * @throws Exception
     */
    @PostMapping("callback/notify")
    public String wxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{

        log.info("\n callback/notify 被调用");

        // 获取微信给我们的回调信息
        ServletInputStream inputStream = request.getInputStream();
        // 将流转换为字符串，转换出来的字符串是XML格式
        String notifyXml = StreamUtils.inputStream2String(inputStream, "utf-8");
       log.info("\n xmlString = \n" + notifyXml);

        // 定义响应微信对象
        HashMap<String, String> returnMap = new HashMap<>();

        // 签名验证：防止伪造回调
        if(WXPayUtil.isSignatureValid(notifyXml, weixinPayProperties.getPartnerKey())){

            // 解析返回结果
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyXml);

            //判断支付是否成功
            if("SUCCESS".equals(notifyMap.get("result_code"))){
                // 获取响应信息
                String totalFee = notifyMap.get("total_fee"); // 获取订单金额
                String outTradeNo = notifyMap.get("out_trade_no"); // 获取订单号
                // 根据订单号 获取订单信息
                Order order = orderService.getOrderByOrderNo(outTradeNo);
                //  校验金额 Integer.parseInt(String) 转换类型      // 支付成功 给微信发送我已接受通知的响应
                if(order != null && order.getTotalFee().intValue() == Integer.parseInt(totalFee)){
                    //接口调用的幂等性：无论接口被调用多少次，最后所影响的结果都是一致的
                    //如果订单的状态已经修改完成
                    if(order.getStatus() == 0){
                        // 更新订单支付状态，并返回成功响应
                        orderService.updateOrderStatus(notifyMap);
                    }
                    returnMap.put("return_code", "SUCCESS");
                    returnMap.put("return_msg", "OK");
                    String returnXml = WXPayUtil.mapToXml(returnMap);
                    response.setContentType("text/xml");
                    log.info("支付成功，通知已处理");
                    return returnXml;
                }
            }
        }
        // 校验失败，返回失败应答
        returnMap.put("return_code", "FAIL");
        returnMap.put("return_msg", "");
        String returnXml = WXPayUtil.mapToXml(returnMap);
        response.setContentType("text/xml");
        log.warn("校验失败");
        return returnXml;
    }

}
