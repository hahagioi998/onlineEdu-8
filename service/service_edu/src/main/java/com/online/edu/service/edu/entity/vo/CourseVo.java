package com.online.edu.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *  用于前端展示课程信息的实体类
 */
@Data
public class CourseVo implements Serializable {

    private static final long serialVersionUID = 1L;
    // 课程ID
    private String id;
    // 课程标题
    private String title;
    // 课程一级分类标题
    private String subjectParentTitle;
    // 课程二级分类标题
    private String subjectTitle;
    // 教师名称
    private String teacherName;
    // 课程的节数
    private Integer lessonNum;
    // 课程的价格
    private String price;
    // 课程封面
    private String cover;
    // 课程销售数量
    private Long buyCount;
    // 课程浏览数量
    private Long viewCount;
    // 课程状态
    private String status;
    // 课程创建时间
    private String gmtCreate;

}
