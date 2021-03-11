package com.online.edu.service.ucenter.service;

import com.online.edu.service.base.dto.MemberCommentDto;
import com.online.edu.service.base.dto.MemberDto;
import com.online.edu.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.ucenter.entity.vo.LoginVo;
import com.online.edu.service.ucenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author Answer
 * @since 2021-02-26
 */
public interface MemberService extends IService<Member> {

    // 会员注册
    void register(RegisterVo registerVo);

    // 会员登陆
    String login(LoginVo loginVo);

   // 根据openid查询数据库 返回用户信息  openid是用户是否微信登陆的唯一凭证
    Member getByOpenid(String openid);

    // 根据会员id查询会员信息 订单
    MemberDto getMemberDtoByMemberId(String memberId);

    // 根据会员id查询会员信息 评论
    MemberCommentDto getMemberCommentDtoByMemberId(String memberId);

    // 根据日期统计注册数量
    Integer countRegisterNum(String day);
}
