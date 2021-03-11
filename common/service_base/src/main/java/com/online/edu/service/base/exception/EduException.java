package com.online.edu.service.base.exception;

import com.online.edu.common.base.result.ResultCodeEnum;
import lombok.Data;

@Data
public class EduException extends RuntimeException {

    //状态码
    private Integer code;

    /**
     * 接受状态码和消息
     * @param code
     * @param message
     */
    public EduException(Integer code, String message) {
        super(message);
        this.code=code;
    }

    /**
     * 接收枚举类型
     * @param resultCodeEnum
     */
    public EduException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "EduException{" +
                "code=" + code +
                "，message=" + this.getMessage() +
                '}';
    }
}
