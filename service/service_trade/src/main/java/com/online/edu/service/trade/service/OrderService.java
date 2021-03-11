package com.online.edu.service.trade.service;

import com.online.edu.service.trade.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author Answer
 * @since 2021-03-01
 */
public interface OrderService extends IService<Order> {

    // 新增订单
    String saveOrder(String courseId, String memberId);

    // 获取订单信息
    Order getByOrderId(String orderId, String memberId);

    // 判断课程是否购买
    Boolean isBuyByCourseId(String courseId, String memberId);

    // 获取当前用户订单列表
    List<Order> selectByMemberId(String memberId);

    // 根据订单id 删除订单
    boolean removeById(String orderId, String memberId);

    // 根据订单号查询订单
    Order getOrderByOrderNo(String orderNo);

    // 更新订单支付状态
    void updateOrderStatus(Map<String, String> notifyMap);

    // 查询支付状态
    boolean queryPayStatus(String orderNo);
}
