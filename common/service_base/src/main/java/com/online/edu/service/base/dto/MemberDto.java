package com.online.edu.service.base.dto;

import lombok.Data;

import java.io.Serializable;

// 用户订单生成数据表
@Data
public class MemberDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;//会员id
    private String mobile;//手机号
    private String nickname;//昵称
}