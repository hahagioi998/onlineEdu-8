package com.online.edu.service.sms.controller;

import com.aliyuncs.exceptions.ClientException;
import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.FormUtils;
import com.online.edu.common.base.util.RandomUtils;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 发送短信验证码
    @GetMapping("send/{mobile}")
    public R getCode(@PathVariable String mobile) throws ClientException {

        //校验手机号是否合法
        if(StringUtils.isEmpty(mobile) || !FormUtils.isMobile(mobile)) {
            log.error("请输入正确的手机号码 ");
            throw new EduException(ResultCodeEnum.LOGIN_PHONE_ERROR);
        }

        //生成验证码
        String checkCode = RandomUtils.getFourBitRandom();
        //发送验证码
        // smsService.send(mobile, checkCode);
        //将验证码存入redis缓存  以便后面得验证  mobile是key   checkCode是值  过期时间5分钟
        redisTemplate.opsForValue().set(mobile, checkCode, 5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }

}
