package com.online.edu.service.edu.entity.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("查询数据的封装-Teacher查询对象")
public class TeacherQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "讲师姓名")
    private String name;
    @ApiModelProperty(value = "讲师级别")
    private Integer level;
    @ApiModelProperty("开始时间")
    private String joinDateBegin;
    @ApiModelProperty("结束时间")
    private String joinDateEnd;

}
