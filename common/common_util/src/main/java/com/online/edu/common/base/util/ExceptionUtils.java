package com.online.edu.common.base.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
// 错误信息打印日志
public class ExceptionUtils {

    public static String getMessage(Exception e){
        // 字符串，用来存储错误栈中的一列信息
        StringWriter sw = null;
        //打印流
        PrintWriter pw = null;

        try{
            //创建字符串对象
            sw = new StringWriter();
            //把这个字符串对象进行打印
            pw = new PrintWriter(sw);
            //把错误信息打印到这个打印流对象中
            e.printStackTrace(pw);
            //清空
            pw.flush();
            sw.flush();

        }finally{
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
