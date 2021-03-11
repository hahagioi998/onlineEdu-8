package com.online.edu.service.edu.mapper;

import com.online.edu.service.edu.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 讲师 Mapper 接口
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Repository
public interface TeacherMapper extends BaseMapper<Teacher> {

}
