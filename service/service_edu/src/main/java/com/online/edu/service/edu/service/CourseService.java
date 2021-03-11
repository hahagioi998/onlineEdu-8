package com.online.edu.service.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.online.edu.service.base.dto.CourseDto;
import com.online.edu.service.edu.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.edu.entity.form.CourseInfoForm;
import com.online.edu.service.edu.entity.vo.*;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface CourseService extends IService<Course> {

    // 添加课程信息
    String saveCurseInfo(CourseInfoForm courseInfoForm);
    // 根据课程ID查询课程数据
    CourseInfoForm getCourseInfoById(String id);
    // 根据课程ID 更新课程数据
    void updateCourseInfoById(CourseInfoForm courseInfoForm);
    // 模糊查询 分页返回数据列表
    IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo);
   //从阿里云 删除课程封面
    boolean removeAvatarById(String id);
   // 删除课程信息
    boolean removeCourseById(String id);

   // 批量删除课程信息
    boolean batchRemoveByIds(List<String> idList);
    // 根据课程ID获取课程的发布页面 所需要的数据
    CoursePublishVo getCoursePublishVoById(String id);
    // 根据课程ID 发布课程即修改课程状态
    boolean publishCourseById(String id);

    // 根据查询条件对象webCourseQueryVo 获取课程信息
    List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo);

    /**
     * 获取课程信息并更新浏览量
     * @param id
     * @return
     */
    WebCourseVo selectWebCourseVoById(String id);

    //获取首页最热门的课程数据（可以规定几条）
    List<Course> selectHotCourse();

    // 根据课程id查询课程信息
    CourseDto getCourseDtoById(String courseId);

    // 更新课程购买数量
    void updateBuyCountById(String id);
}
