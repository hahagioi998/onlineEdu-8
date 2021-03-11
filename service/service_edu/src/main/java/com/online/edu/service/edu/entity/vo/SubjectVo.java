package com.online.edu.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 将数据库中课程一级二级标题 嵌套的形式显示在前端
@Data
public class SubjectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Integer sort;
    //父类别名包含子类别，子类别做子关联嵌套
    private List<SubjectVo> children = new ArrayList<>();
}
