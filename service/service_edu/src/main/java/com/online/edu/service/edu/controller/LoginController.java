package com.online.edu.service.edu.controller;

import com.online.edu.common.base.result.R;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@RestController
@RequestMapping("/user")
public class LoginController {

    // 后台管理系统的登陆 退出
    /**
     * 登陆功能
     * @return
     */
    @PostMapping("login")
    public R login(){
        return R.ok().data("token","admin");
    }

    /**
     * 呆着登陆凭借 获取用户信息
     * @return
     */
    @GetMapping("info")
    public R info(){
        //这里要返回用户信息 ，有三个字段：name用户名字段，roles角色列表,avatar用户头像
        return R.ok().data("name","admin")
                .data("roles","[admin]")
                .data("avatar","http://images.nowcoder.com/head/3t.png");
    }
    /**
     * 退出登录，注销
     */
    @PostMapping("logout")
    public R logout(){
        return R.ok();
    }

}
