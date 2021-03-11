package com.online.edu.service.ucenter.controller.api;

import com.google.gson.Gson;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.ExceptionUtils;
import com.online.edu.common.base.util.HttpClientUtils;
import com.online.edu.common.base.util.JwtInfo;
import com.online.edu.common.base.util.JwtUtils;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.ucenter.entity.Member;
import com.online.edu.service.ucenter.service.MemberService;
import com.online.edu.service.ucenter.utils.UcenterProperties;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@CrossOrigin
@Api( tags = "微信登陆管理")
@Controller//注意这里没有配置 @RestController
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class ApiWxController {

    @Autowired
    private UcenterProperties ucenterProperties;

    @Autowired
    private MemberService memberService;

    //  获取微信二维码url
    @GetMapping("login")
    public String genQrConnect(HttpSession session){

        // 组装url：https://open.weixin.qq.com/connect/grconnect?appid=APPID&redirect_uri=回调地址&response_type=code&scope=snsapi_login&state=随机数#wechat_redirect
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //处理回调url
        String redirecturi = "";
        try {
            // 将url地址用UTF-8的转码形式转码，替换其中的特殊字符
            redirecturi = URLEncoder.encode(ucenterProperties.getRedirectUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new EduException(ResultCodeEnum.URL_ENCODE_ERROR);
        }
        //处理state：生成随机数，存入session
        String state = UUID.randomUUID().toString().replace("-","");
        log.info("生成 state = " + state);
        // spring 会把这个session 存到redis中  不需要多写代码
        session.setAttribute("wx_open_state",state);

        // 填充数据，先给一个需要填充的字符串，在添加数据
        String qrcodeUrl = String.format(
                baseUrl,
                ucenterProperties.getAppId(),
                redirecturi,
                state
        );

        // 页面跳转
        return "redirect:" + qrcodeUrl;
    }


    // 扫码 确认登陆后 执行  回调接口  这里：我们传的是response_type=code，他会返回给我们一个参数code，state字符串
    // 注意回调后这里执行的是 谷粒商城了代码 guli.shop/api/ucenter/wx/callback8160 这个方法，这个方法中会调用我们这里定义的方法，看文档。这是个中间的调用方法
    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session){

        //回调被拉起，并获得code和state参数  这两个参数是从url地址栏中获得的
        System.out.println("callback被调用");
        System.out.println("code = " + code);
        System.out.println("state = " + state);

        // 判断参数是否为空
        if(StringUtils.isEmpty(code)||StringUtils.isEmpty(state)){
            log.error("非法回调请求");
            throw new EduException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        // 校验state是否正确 防止CSRF攻击
        // 注意：我们引用了spring-session-data-redi 对session进行了封装 session.getAttribute()实际上是会从redis中取
        String wxOpenState = (String)session.getAttribute("wx_open_state");
        // 进行判断 是否一致
        if(!state.equals(wxOpenState)){
            log.error("非法回调请求");
            throw new EduException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //携带授权临时票据code，和appid以及appsecret请求access_token和opedId(微信唯一标识)
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        // 组装参数: ?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        // 使用工具栏HttpClientUtils 组装参数
        Map<String, String> accessTokenParam = new HashMap();
        accessTokenParam.put("appid", ucenterProperties.getAppId());
        accessTokenParam.put("secret", ucenterProperties.getAppSecret());
        accessTokenParam.put("code", code);
        accessTokenParam.put("grant_type", "authorization_code");
        // 通过携带授权临时票据code，和appid以及appsecret请求access_token和opedId(微信唯一标识) 获取返回结果
        HttpClientUtils client = new HttpClientUtils(accessTokenUrl, accessTokenParam);
        String result = "";
        try {
            //发送请求
            client.get();
            // 获得响应 请求结果
            result = client.getContent();
            System.out.println("result = " + result);
        } catch (Exception e) {
            log.error("获取access_token失败");
            throw new EduException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        // 获取的result是json格式，把他转换为map
        Gson gson = new Gson();
        HashMap<String, Object> resultMap = gson.fromJson(result, HashMap.class);
        //判断微信获取access_token失败的响应  {" errcode" :40829, "errasg":"invalid code"}
        Object errcodeObj = resultMap.get("errcode");
        if(errcodeObj != null){
            String errmsg = (String)resultMap.get("errmsg");
            Double errcode = (Double)errcodeObj;  // Double类型
            log.error("获取access_token失败 - " + "message: " + errmsg + ", errcode: " + errcode);
            throw new EduException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //微信获取access_token响应成功
        String accessToken = (String)resultMap.get("access_token");
        String openid = (String)resultMap.get("openid");
        log.info("accessToken = " + accessToken);
        log.info("openid = " + openid);
        //在本地数据库中查找当前用户的信息
        Member member = memberService.getByOpenid(openid);
        // 判断微信当前用户是否已经注册  后端判断对象为空 member == null
         if(member == null){
             //如果当前用户不存在 根据access_token获取微信用户个人的基本信息
             //向微信的资源服务器发起请求，获取当前用户的用户信息
             String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo";
             Map<String, String> baseUserInfoParam = new HashMap();
             baseUserInfoParam.put("access_token", accessToken);
             baseUserInfoParam.put("openid", openid);
             // 使用工具类拼装url参数并且发送get请求
             client = new HttpClientUtils(baseUserInfoUrl, baseUserInfoParam);
             String resultUserInfo = null;
             try {
                 client.get();
                 resultUserInfo = client.getContent();
             } catch (Exception e) {
                 log.error("获取用户信息失败"+ ExceptionUtils.getMessage(e));
                 throw new EduException(ResultCodeEnum.FETCH_USERINFO_ERROR);
             }
             // 把对象 json格式转换为map格式
             HashMap<String, Object> resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
             // 判断响应结果是否错误
             errcodeObj = resultUserInfoMap.get("errcode");
             if(errcodeObj != null){
                 String errmsg = (String)resultMap.get("errmsg");
                 Double errcode = (Double)errcodeObj;  // Double类型
                 log.error("获取用户信息失败： " + "errcode: " + errcode + ", message: " + errmsg);
                 throw new EduException(ResultCodeEnum.FETCH_USERINFO_ERROR);
             }
             //若响应结果正确，解析出结果的用户个人信息  需要什么去除什么
             String nickname = (String)resultUserInfoMap.get("nickname");
             String headimgurl = (String)resultUserInfoMap.get("headimgurl");
             Double sex = (Double)resultUserInfoMap.get("sex");  // 性别为Double
             // 用户注册  在本地数据库中插入当前微信用户的信息(使用微信账号在本地服务器注册新用户）
             member = new Member();
             member.setOpenid(openid);
             member.setNickname(nickname);
             member.setAvatar(headimgurl);
             // 转换为整形
             member.setSex(sex.intValue());
             // 存入数据库后，会自动生成id
             memberService.save(member);
         }
        // 这里不用再取出数据库里的值获取数据的主键id了。我们再往数据库里存的时候，就已经为id赋值了，赋值后，再存的id
        // 直接使用当前用户的信息登陆，生成jwt
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setId(member.getId());
        jwtInfo.setNickname(member.getNickname());
        jwtInfo.setAvatar(member.getAvatar());
        // 生成jwtToken
        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 1800);

        // 完成后，跳转页面到首页 携带token跳转
        return "redirect:http://localhost:3333?token=" + jwtToken;
    }
}
