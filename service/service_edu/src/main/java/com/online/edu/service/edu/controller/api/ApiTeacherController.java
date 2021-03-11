package com.online.edu.service.edu.controller.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.entity.vo.TeacherQueryVo;
import com.online.edu.service.edu.feign.OssFileService;
import com.online.edu.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author Answer
 * @since 2020-11-23  
 */

//@CrossOrigin  //允许跨域访问
@Api( tags = "前端讲师")
@RestController
@RequestMapping("/api/edu/teacher")
public class ApiTeacherController {

    @Autowired
    private TeacherService teacherService;

    // 获取教师数据列表
    @ApiOperation(value="所有讲师列表")
    @GetMapping("list")
    public R listAll(){
        // 获取讲师数据列表 调用IService方法
        List<Teacher> list = teacherService.list(null);
        return R.ok().data("items", list).message("获取讲师列表成功");
    }

    // 讲师页面数据展示
    @ApiOperation(value = "获取讲师")
    @GetMapping("get/{id}")
    public R get(
            @ApiParam(value = "讲师ID", required = true)
            @PathVariable String id) {
        Map<String, Object> map = teacherService.selectTeacherInfoById(id);
        return R.ok().data(map);
    }
}

