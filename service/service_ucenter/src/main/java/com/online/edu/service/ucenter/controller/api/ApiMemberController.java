package com.online.edu.service.ucenter.controller.api;


import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.JwtInfo;
import com.online.edu.common.base.util.JwtUtils;
import com.online.edu.service.base.dto.MemberCommentDto;
import com.online.edu.service.base.dto.MemberDto;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.ucenter.entity.vo.LoginVo;
import com.online.edu.service.ucenter.entity.vo.RegisterVo;
import com.online.edu.service.ucenter.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author Answer
 * @since 2021-02-26
 */
@Api(tags = "会员管理")
//@CrossOrigin
@RestController
@RequestMapping("/api/ucenter/member")
@Slf4j
public class ApiMemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "会员注册")
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        memberService.register(registerVo);
        return R.ok();
    }

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo) {
        String token = memberService.login(loginVo);
        return R.ok().data("token", token).message("登陆成功！");
    }

    // 验证身份信息  根据token获取登录信息
    @ApiOperation(value = "根据token获取登录信息")
    @GetMapping("get-login-info")
    public R getLoginInfo(HttpServletRequest request){
        // 从request头中获取令牌token
        try{
            JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
            if(StringUtils.isEmpty(jwtInfo.toString())) {
                throw new EduException(ResultCodeEnum.LOGIN_AUTH);
            }
            return R.ok().data("userInfo", jwtInfo);
        }catch (Exception e){
            log.error("解析用户信息失败：" + e.getMessage());
            throw new EduException(ResultCodeEnum.FETCH_USERINFO_ERROR);
        }
    }

    @ApiOperation("根据会员id查询会员信息，用于用户订单")
    @GetMapping("inner/get-member-dto/{memberId}")
    public MemberDto getMemberDtoByMemberId(
            @ApiParam(value = "会员ID", required = true)
            @PathVariable String memberId){
        MemberDto memberDto = memberService.getMemberDtoByMemberId(memberId);
        return memberDto;
    }

    @ApiOperation("根据会员id查询会员信息,用于用户评论")
    @GetMapping("inner/get-memberCommen-dto/{memberId}")
    public MemberCommentDto getMemberCommentDtoByMemberId(
            @ApiParam(value = "会员ID", required = true)
            @PathVariable String memberId){
        MemberCommentDto memberCommentDto = memberService.getMemberCommentDtoByMemberId(memberId);
        return memberCommentDto;
    }

}

