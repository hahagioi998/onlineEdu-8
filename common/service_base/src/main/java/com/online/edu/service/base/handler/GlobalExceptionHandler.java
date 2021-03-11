package com.online.edu.service.base.handler;

import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.ExceptionUtils;
import com.online.edu.service.base.exception.EduException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理  ControllerAdvice 处理controller的异常信息
 * 注意：默认的e.printStackTrace()，是 printStackTrace(System.err);把信息输出到控制台上，我们要把信息野打印到文件中需要再做配置
 * 我们价格日志记录器的注解 :@Slf4j  这个注解是用来操控log对象的
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //全局异常处理  报统一化的错误信息
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
//        e.printStackTrace();
        //打印错误信息  log对象会遵照我们配置的规则去操控对象，打印出来的日志信息是字符串不是错误跟踪栈，这里我们导入一个错误跟踪类
//        log.error(e.getMessage());
        log.error(ExceptionUtils.getMessage(e));
        return R.error();
    }

    //sql相关部分（jdbc，实体类，xml,mapper,dao等有关数据库的部份写错了）
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseBody
    public R error(BadSqlGrammarException e){
//        e.printStackTrace();
        log.error(ExceptionUtils.getMessage(e));
        return R.setResult(ResultCodeEnum.BAD_SQL_GRAMMAR);
    }

    //前端传递的值不是json格式时报错：HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R error(HttpMessageNotReadableException e){
//        e.printStackTrace();
//        log.error(e.getMessage());
        log.error(ExceptionUtils.getMessage(e));
        return R.setResult(ResultCodeEnum.JSON_PARSE_ERROR);
    }

    //自定义异常处理
    @ExceptionHandler(EduException.class)
    @ResponseBody
    public R error(EduException e){
        log.error(ExceptionUtils.getMessage(e));
        // 这里return 的 都是给前端返回的消息
        return R.error().message(e.getMessage()).code(e.getCode());
    }



}
