package com.online.edu.service.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.service.base.dto.CourseDto;
import com.online.edu.service.base.dto.MemberDto;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.trade.entity.Order;
import com.online.edu.service.trade.entity.PayLog;
import com.online.edu.service.trade.feign.EduCourseService;
import com.online.edu.service.trade.feign.UcenterMemberService;
import com.online.edu.service.trade.mapper.OrderMapper;
import com.online.edu.service.trade.mapper.PayLogMapper;
import com.online.edu.service.trade.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.edu.service.trade.util.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2021-03-01
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduCourseService eduCourseService;

    @Autowired
    private UcenterMemberService ucenterMemberService;

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public String saveOrder(String courseId, String memberId) {

        // 查询当前用户是否已有当前课程的订单 在订单表中
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("member_id", memberId);
        Order orderExist = baseMapper.selectOne(queryWrapper);
        if(orderExist != null){
            return orderExist.getId(); //如果订单已存在，则直接返回订单id
        }
        // 根据课程ID查询课程信息
        CourseDto courseDto = eduCourseService.getCourseDtoById(courseId);
        if (courseDto == null) {
            throw new EduException(ResultCodeEnum.PARAM_ERROR);
        }
        //查询用户ID查询用户信息
        MemberDto memberDto = ucenterMemberService.getMemberDtoByMemberId(memberId);
        if (memberDto == null) {
            throw new EduException(ResultCodeEnum.PARAM_ERROR);
        }
        //创建订单
        Order order = new Order();
        // 创建订单号
        order.setOrderNo(OrderNoUtils.getOrderNo());
        // 课程ID
        order.setCourseId(courseId);
        // 课程标题
        order.setCourseTitle(courseDto.getTitle());
        //课程封面
        order.setCourseCover(courseDto.getCover());
        // 教师名称
        order.setTeacherName(courseDto.getTeacherName());
        // 创建的数据库里的金额是分 ，我们购买的金额是元  我们需要把获取的金额×100  private BigDecimal price BigDecimal.multiply(new BigDecimal(100))
        order.setTotalFee(courseDto.getPrice().multiply(new BigDecimal(100)));//分 微信支付的时候 规定的类型位分
        //order.setTotalFee(courseDto.getPrice());
        order.setMemberId(memberId);
        order.setMobile(memberDto.getMobile());
        order.setNickname(memberDto.getNickname());

        order.setStatus(0);//未支付 0：代表未支付 1:代表已购买
        order.setPayType(1);//微信支付 1：代表使用微信支付 2：后期可以整合支付宝
        baseMapper.insert(order);
        return order.getId();
    }

    @Override
    public Order getByOrderId(String orderId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("id", orderId)
                .eq("member_id", memberId);
        Order order = baseMapper.selectOne(queryWrapper);
        return order;
    }

    @Override
    public Boolean isBuyByCourseId(String courseId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("member_id", memberId)
                .eq("course_id", courseId)
                .eq("status", 1);  // 0：代表未支付 1:代表已购买
        Integer count = baseMapper.selectCount(queryWrapper);
        return count.intValue() > 0;

    }

    @Override
    public List<Order> selectByMemberId(String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        // 按课程创建时间倒序排序
        queryWrapper.orderByDesc("gmt_create");
        queryWrapper.eq("member_id", memberId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean removeById(String orderId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("id", orderId)
                .eq("member_id", memberId);
        return this.remove(queryWrapper);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return baseMapper.selectOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(Map<String, String> notifyMap) {

        //更新订单状态
        String orderNo = notifyMap.get("out_trade_no"); // 获取订单号
        Order order = this.getOrderByOrderNo(orderNo);  // 根据订单号 获取订单信息
        order.setStatus(1);//支付成功        修改订单状态
        baseMapper.updateById(order);

        //记录支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(Long.parseLong(notifyMap.get("total_fee")));//总金额(分)
        payLog.setTradeState(notifyMap.get("result_code"));//支付状态
        payLog.setTransactionId(notifyMap.get("transaction_id"));  //支付流水号
        payLog.setAttr(new Gson().toJson(notifyMap));  //订单的所有信息都存在这个
        payLogMapper.insert(payLog);

        //更新课程销量：有问题直接熔断
        eduCourseService.updateBuyCountById(order.getCourseId());
    }

    @Override
    public boolean queryPayStatus(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        Order order = baseMapper.selectOne(queryWrapper);
        return order.getStatus() == 1;
    }
}
