package com.online.edu.service.edu.mapper;

import com.online.edu.service.edu.entity.CourseDescription;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 课程简介 Mapper 接口
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Repository  //@Repository用在持久层的接口上，这个注解是将接口的一个实现类交给spring管理
public interface CourseDescriptionMapper extends BaseMapper<CourseDescription> {

}
