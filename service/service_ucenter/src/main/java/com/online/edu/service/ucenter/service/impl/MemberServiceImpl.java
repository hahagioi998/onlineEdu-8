package com.online.edu.service.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.FormUtils;
import com.online.edu.common.base.util.JwtInfo;
import com.online.edu.common.base.util.JwtUtils;
import com.online.edu.common.base.util.MD5;
import com.online.edu.service.base.dto.MemberCommentDto;
import com.online.edu.service.base.dto.MemberDto;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.ucenter.entity.Member;
import com.online.edu.service.ucenter.entity.vo.LoginVo;
import com.online.edu.service.ucenter.entity.vo.RegisterVo;
import com.online.edu.service.ucenter.mapper.MemberMapper;
import com.online.edu.service.ucenter.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2021-02-26
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 会员注册
     * @param registerVo
     */
    @Override
    public void register(RegisterVo registerVo) {

        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        // 判断手机号是否合法
        if(StringUtils.isEmpty(mobile)
                || !FormUtils.isMobile(mobile)){
            throw new EduException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }

        //校验参数
        if (StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code)
                || StringUtils.isEmpty(nickname)) {
            throw new EduException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验验证码  校验码从redis中取值 尽量减少对数据库得访问
        String checkCode = (String)redisTemplate.opsForValue().get(mobile);
        if(!code.equals(checkCode)){
            throw new EduException(ResultCodeEnum.CODE_ERROR);
        }

        //判断用户是否是否被注册 根据手机号判断   mobile从数据库中mysql取值
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new EduException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }

        //注册账户
        Member member = new Member();
        member.setNickname(nickname);
        member.setMobile(mobile);
        // 密码进行MD5加密
        member.setPassword(MD5.encrypt(password));
        // 账号进行封号 （平台封号）
        member.setDisabled(false);
        // 使用随机头像
        //member.setAvatar(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        member.setAvatar("https://onlinedu-file.oss-cn-beijing.aliyuncs.com/default.jpg");
        baseMapper.insert(member);

    }

    @Override
    public String login(LoginVo loginVo) {
        // 取值
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // 校验参数是否为空,手机号是否符合规定
        if (StringUtils.isEmpty(mobile)
                || !FormUtils.isMobile(mobile)
                || StringUtils.isEmpty(password)) {
            throw new EduException(ResultCodeEnum.LOGIN_PARAM_ERROR);
        }
        //校验手机号是否存在，账号是否已注册
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("mobile",mobile);
        Member member = baseMapper.selectOne(memberQueryWrapper);
        // 若手机号不存在，账户没被注册
        if(member== null){
            throw new EduException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }
        // 校验密码是否正确
        String memberPassword = member.getPassword();
        if(!MD5.encrypt(password).equals(memberPassword)){
            throw new EduException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //校验用户是否被禁用
        if(member.getDisabled()){
            throw new EduException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 验证完成后，完成登陆，生成token
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setAvatar(member.getAvatar());
        jwtInfo.setId(member.getId());
        jwtInfo.setNickname(member.getNickname());
        // 生成token 设置过期时间1800 是半个小时 60*30 =1800  以秒算的
        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 1800);

        return jwtToken;
    }

    @Override
    public Member getByOpenid(String openid) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {
        Member member = baseMapper.selectById(memberId);
        MemberDto memberDto = new MemberDto();
        BeanUtils.copyProperties(member, memberDto);
        return memberDto;
    }

    @Override
    public MemberCommentDto getMemberCommentDtoByMemberId(String memberId) {
        Member member = baseMapper.selectById(memberId);
        MemberCommentDto memberCommentDto = new MemberCommentDto();
        BeanUtils.copyProperties(member, memberCommentDto);
        return memberCommentDto;
    }

    @Override
    public Integer countRegisterNum(String day) {
        return baseMapper.selectRegisterNumByDay(day);
    }
}
