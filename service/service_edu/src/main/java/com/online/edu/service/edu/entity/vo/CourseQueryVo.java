package com.online.edu.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *  前端的查询数据  form列表 迷糊查询
 */
@Data
public class CourseQueryVo implements Serializable {

    // Serializable 实现序列化  远程调用的时候使用，这里spring cloud中不需要进行序列化。但是第三方的一些插件需要如：dubbo框架远程调用
    private static final long serialVersionUID = 1L;

    // 课程标题
    private String title;
    // 讲师ID
    private String teacherId;
    //课程分类的一级类别
    private String subjectParentId;
    //课程分类的二级类别
    private String subjectId;


}
