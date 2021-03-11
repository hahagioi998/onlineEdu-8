package com.online.edu.service.edu.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.service.base.dto.CourseDto;
import com.online.edu.service.edu.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.edu.service.edu.entity.vo.CoursePublishVo;
import com.online.edu.service.edu.entity.vo.CourseVo;
import com.online.edu.service.edu.entity.vo.WebCourseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {

    // List<CourseVo> selectPageByCourseQueryVo(Page<CourseVo> pageParam);
    // 分页条件 多表查询
   // List<CourseVo> selectPageByCourseQueryVo(Page<CourseVo> pageParam, QueryWrapper<CourseVo> queryWrapper);

    List<CourseVo> selectPageByCourseQueryVo(
            //mp会自动组装分页参数
             Page<CourseVo> pageParam,
            //mp会自动组装queryWrapper：
            //@Param(Constants.WRAPPER) 和 xml文件中的 ${ew.customSqlSegment} 对应 。组装查询条件
            @Param(Constants.WRAPPER) QueryWrapper<CourseVo> queryWrapper);
    // 多表查询关联 后台网站前端页面 发布课程时的数据
    CoursePublishVo selectCoursePublishVoById(String id);
    // 多表联查 前台页面 课程详情的数据
    WebCourseVo selectWebCourseVoById(String courseId);

    // 多表联查 课程信息 教师名称
    CourseDto selectCourseDtoById(String courseId);
}
