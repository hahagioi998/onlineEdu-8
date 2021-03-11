package com.online.edu.service.cms.entity.vo;

import lombok.Data;

@Data
public class AdVo {
    private static final long serialVersionUID=1L;
    // 广告ID
    private String id;
    //广告标题
    private String title;
    // 广告排序
    private Integer sort;
    //广告类型的标题
    private String type;
}
