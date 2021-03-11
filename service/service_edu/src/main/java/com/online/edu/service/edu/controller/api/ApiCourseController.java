package com.online.edu.service.edu.controller.api;

import com.online.edu.common.base.result.R;
import com.online.edu.service.base.dto.CourseDto;
import com.online.edu.service.edu.entity.Course;
import com.online.edu.service.edu.entity.vo.ChapterVo;
import com.online.edu.service.edu.entity.vo.WebCourseQueryVo;
import com.online.edu.service.edu.entity.vo.WebCourseVo;
import com.online.edu.service.edu.service.ChapterService;
import com.online.edu.service.edu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin  //允许跨域访问
@Api( tags = "前端课程")
@RestController
@RequestMapping("/api/edu/course")
public class ApiCourseController {


    @Autowired
    private CourseService courseService;
    @Autowired
    private ChapterService chapterService;


    // 根据查询条件 查询对象
    @ApiOperation("课程列表")
    @GetMapping("list")
    public R list(@ApiParam(value = "查询对象", required = false)
                              WebCourseQueryVo webCourseQueryVo){
        // 根据条件 查询课程列表 返回的是课程数据
        List<Course> courseList = courseService.webSelectList(webCourseQueryVo);
        return  R.ok().data("courseList", courseList);
    }

    @ApiOperation("根据ID查询课程")
    @GetMapping("get/{courseId}")
    public R getById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId){

        //查询课程信息和讲师信息
        WebCourseVo webCourseVo = courseService.selectWebCourseVoById(courseId);
        //查询当前课程的章节信息
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("course", webCourseVo).data("chapterVoList", chapterVoList);
    }

    // 这个方法 返回结果不是给前端的 是给内部微服务的 可以直接返回对象
    @ApiOperation("根据课程id查询课程信息")
    @GetMapping("inner/get-course-dto/{courseId}")
    public CourseDto getCourseDtoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId){
        CourseDto courseDto = courseService.getCourseDtoById(courseId);
        return courseDto;
    }


    @ApiOperation("根据课程id更改销售量")
    @GetMapping("inner/update-buy-count/{id}")
    public R updateBuyCountById(
            @ApiParam(value = "课程id", required = true)
            @PathVariable String id){
        courseService.updateBuyCountById(id);
        return R.ok();
    }


}
