package com.online.edu.service.edu.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.entity.Course;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.entity.form.CourseInfoForm;
import com.online.edu.service.edu.entity.vo.CoursePublishVo;
import com.online.edu.service.edu.entity.vo.CourseQueryVo;
import com.online.edu.service.edu.entity.vo.CourseVo;
import com.online.edu.service.edu.entity.vo.TeacherQueryVo;
import com.online.edu.service.edu.service.CourseService;
import com.online.edu.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
//@CrossOrigin  //允许跨域访问
@Api( tags = "课程管理")
@RestController
@Slf4j
@RequestMapping("/admin/edu/course")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private VideoService videoService;

    @ApiOperation("新增课程")
    @PostMapping("save-course-info")
    public R saveCourseInfo(@ApiParam(value = "课程基本信息", required = true)
                            @RequestBody CourseInfoForm courseInfoForm){

        String courseId = courseService.saveCurseInfo(courseInfoForm);
        return R.ok().data("courseId", courseId).message("保存成功");
    }

    @ApiOperation("根据ID查询课程")
    @GetMapping("course-info/{id}")
    public R getById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){
         // 自定义业务方法，查询数据
        CourseInfoForm courseInfoForm = courseService.getCourseInfoById(id);
        if (courseInfoForm != null) {
            return R.ok().data("item", courseInfoForm);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("更新课程")
    @PutMapping("update-course-info")
    public R updateCourseInfoById(
            @ApiParam(value = "课程基本信息", required = true)
            @RequestBody CourseInfoForm courseInfoForm){

        courseService.updateCourseInfoById(courseInfoForm);
        return R.ok().message("修改成功");
    }


    //分页查询教师信息
    @ApiOperation("分页课程列表")
    @GetMapping("list/{page}/{limit}")
    public R index(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(value = "查询对象")
                    CourseQueryVo courseQueryVo){

        IPage<CourseVo> pageModel = courseService.selectPage(page, limit, courseQueryVo);
        List<CourseVo> records = pageModel.getRecords();
        // 注意：MyBatis-Plus是自动在我们分页查询时，帮我们查询了数据总数量total，
        long total = pageModel.getTotal();
        return  R.ok().data("total", total).data("rows", records);
    }

    //根据ID  删除课程信息
    @ApiOperation(value = "根据id删除课程",notes = "逻辑删除，修改is_delete值为1，即删除成功")  //value 是 大概信息 ，notes是详细信息
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam("课程ID") @PathVariable("id") String id){


        //此处要用阿里云VOD中删除视频文件的接口
        videoService.removeMediaVideoByCourseId(id);

        //删除课程封面
        boolean b = courseService.removeAvatarById(id);

        //删除课程
        boolean remove = courseService.removeCourseById(id);
        if(remove){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    //根据ID列表  批量删除课程信息
    @ApiOperation(value = "根据id批量删除课程",notes = "逻辑删除，修改is_delete值为1，即删除成功")  //value 是 大概信息 ，notes是详细信息
    @DeleteMapping("batch-remove")
    public R removeRows(@ApiParam(value = "课程ID列表",required = true)
                        @RequestBody List<String> idList){

        // 判断idList是否为空
        boolean remove = courseService.batchRemoveByIds(idList);
        if(remove){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }
    // 通过课程ID获取课程发布信息
    @ApiOperation("根据ID获取课程发布信息")
    @GetMapping("course-publish/{id}")
    public R getCoursePublishVoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){

        CoursePublishVo coursePublishVo = courseService.getCoursePublishVoById(id);
        if (coursePublishVo != null) {
            return R.ok().data("item", coursePublishVo);
        } else {
            return R.error().message("数据不存在");
        }
    }

    //根据id发布课程  所谓的发布课程就是修改课程的状态course.status
    @ApiOperation("根据id发布课程")
    @PutMapping("publish-course/{id}")
    public R publishCourseById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){

        boolean result = courseService.publishCourseById(id);
        if (result) {
            return R.ok().message("发布成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

}

