package com.online.edu.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChapterVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Integer sort;
    // 存贮的是视频VO ，因为一个章节下有多个视频所以是1对多
    private List<VideoVo> children = new ArrayList<>();
}
