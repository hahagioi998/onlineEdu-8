package com.online.edu.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

// 前端浏览器 课程页面展示的数据
@Data
public class WebCourseQueryVo implements Serializable {
    //前端页面展示数据 根据页面选择 需要展示的数据

    private static final long serialVersionUID = 1L;
    // 一级课程类别
    private String subjectParentId;
    // 二级课程类别
    private String subjectId;
    // 销售数量  最热
    private String buyCountSort;
    // 创建时间  最新
    private String gmtCreateSort;
    // 价格
    private String priceSort;
    //价格正序：1，价格倒序：2
    private Integer type;
}
