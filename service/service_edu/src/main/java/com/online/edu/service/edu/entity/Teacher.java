package com.online.edu.service.edu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.online.edu.service.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AliasFor;

/**
 * <p>
 * 讲师
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("edu_teacher")
@ApiModel(value="Teacher对象", description="讲师")
public class Teacher extends BaseEntity {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "讲师姓名",example = "李老师")
    private String name;

    @ApiModelProperty(value = "讲师简介",example = "北大博士后")
    private String intro;

    @ApiModelProperty(value = "讲师资历,一句话说明讲师",example = "金牌讲师")
    private String career;

    @ApiModelProperty(value = "头衔 1高级讲师 2首席讲师",example = "2")
    private Integer level;

    @ApiModelProperty(value = "讲师头像")
    private String avatar;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "入驻时间",example = "2020-11-11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date joinDate;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
