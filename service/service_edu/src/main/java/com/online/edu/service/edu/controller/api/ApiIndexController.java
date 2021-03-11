package com.online.edu.service.edu.controller.api;

import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.entity.Course;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.service.CourseService;
import com.online.edu.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@CrossOrigin
@Api(tags = "首页")
@RestController
@RequestMapping("/api/edu/index")
public class ApiIndexController {

    // 前端页面需要展示课程数据，教师数据
    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherService teacherService;

//    如果 想要/api/edu/index 在这个路径下直接执行方法，就不需要要@GetMapping后面添加数据
    @ApiOperation("前端课程和讲师的数据列表")
    @GetMapping
    public R index(){
        // 获取热门课程数据
        //获取首页最热门前8条课程数据
        List<Course> courseList = courseService.selectHotCourse();
        //获取首页推荐前4条讲师数据
        List<Teacher> teacherList = teacherService.selectHotTeacher();

        return R.ok().data("courseList", courseList).data("teacherList", teacherList);


    }

}
