package com.online.edu.service.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
// 数据传输course对象  当订单模块调用edu模块获取course信息使，edu返回一个courseDto对象，不用返回course对象，因为course对象trade模块中不存在
@Data
public class CourseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;//课程ID
    private String title;//课程标题
    private BigDecimal price;//课程销售价格，设置为0则可免费观看
    private String cover;//课程封面图片路径
    private String teacherName;//课程讲师
}