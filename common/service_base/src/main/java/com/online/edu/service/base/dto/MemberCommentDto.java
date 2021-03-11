package com.online.edu.service.base.dto;

import lombok.Data;

import java.io.Serializable;

// 用户评论数据
@Data
public class MemberCommentDto implements Serializable {

    private static final long serialVersionUID = 1L;
    // 会员ID
    private String id;
    // 会员名称
    private String nickname;
    //会员头像
    private String avatar;
}
