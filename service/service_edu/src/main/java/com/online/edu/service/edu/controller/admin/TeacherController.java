package com.online.edu.service.edu.controller.admin;


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
@Api( tags = "讲师管理")
@RestController
@RequestMapping("/admin/edu/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private OssFileService ossFileService;

    //查询讲师列表
    @ApiOperation("所有讲师列表")
    @GetMapping("list")
    public R listAll(){
        List<Teacher> list = teacherService.list();

        //通过类名调用静态方法  返回值是R，可以调用普通方法存入数据
        return R.ok().data("items",list);
    }

    //根据ID  删除教师信息
    @ApiOperation(value = "根据id删除讲师",notes = "逻辑删除，修改is_delete值为1，即删除成功")  //value 是 大概信息 ，notes是详细信息
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam("讲师ID") @PathVariable("id") String id){
        //删除讲师头像
        boolean b = teacherService.removeAvatarById(id);

        //删除讲师
        boolean remove = teacherService.removeById(id);
        if(remove){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    //分页查询教师信息
    @ApiOperation(value = "讲师分页列表",notes = "查询讲师信息，分页显示")
    @GetMapping("list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码",required = true) @PathVariable("page") Long page,
                      @ApiParam(value = "每页记录数",required = true) @PathVariable("limit") Long limit,
                      @ApiParam("讲师查询列表查询对象") TeacherQueryVo teacherQueryVo){
        /**
         * MyBatis-Plus分页查询 创建MyBatis-Plus再带的page对象
         */
        //创建page对象   mybatis-plus自带的page类
        Page<Teacher> teacherPage = new Page<>(page, limit);
        // 进行分页查询 teacherPage:分页的信息  自定义的方法
        IPage<Teacher> pageModel = teacherService.selectPage(teacherPage, teacherQueryVo);
        //获取分页查询到的数据
        List<Teacher> teacherList = pageModel.getRecords();
        //获取总记录数
        long total = pageModel.getTotal();
        return R.ok().data("items",teacherList).data("total",total);
    }

    //新增讲师   注意：在restf风格中，post格式提交数据，对象类型用json方式提交 使用注解@RequestBody 即可
    @ApiOperation("新增讲师")
    @PostMapping("/save")
    public R save(@ApiParam("新增讲师对象") @RequestBody Teacher teacher){
        //插入数据的时候要自动填充时间
        boolean result = teacherService.save(teacher);
        if(result){
            return R.ok().message("添加成功");
        }else{
            return R.error().message("添加失败");
        }
    }

    //更新讲师信息
    @ApiOperation("更新讲师")
    @PutMapping("/update")
    public R updateById(@ApiParam("新增讲师对象") @RequestBody Teacher teacher){
        boolean result = teacherService.updateById(teacher);
        if(result){
            return R.ok().message("修改成功");
        }else {
            return R.error().message("添加失败，该讲师不存在");
        }
    }

    //根据id获取讲师信息
    @ApiOperation("根据id获取讲师信息")
    @GetMapping("/get/{id}")
    public R getById(@ApiParam(value = "讲师ID",required = true) @PathVariable String id ){
        Teacher teacher = teacherService.getById(id);
        if(teacher != null){
            return R.ok().data("item", teacher);
        }else{
            return R.error().message("数据不存在");
        }
    }

    //根据ID列表  批量删除教师信息
    @ApiOperation(value = "根据id批量删除讲师",notes = "逻辑删除，修改is_delete值为1，即删除成功")  //value 是 大概信息 ，notes是详细信息
    @DeleteMapping("batch-remove")
    public R removeRows(@ApiParam(value = "讲师ID列表",required = true)
                            @RequestBody List<String> idList){
        boolean remove = teacherService.removeByIds(idList);
        if(remove){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    //查询讲师列表
    @ApiOperation("根据关键词查询讲师名列表")
    @GetMapping("list/name/{key}")
    public R selectNameListByKey(
            @ApiParam(value = "关键词",required = true)
            @PathVariable("key") String key){
        List<Map<String,Object>> nameList = teacherService.selectNameList(key);

        //nameList.forEach(System.out::println);

        //通过类名调用静态方法  返回值是R，可以调用普通方法存入数据
        return R.ok().data("nameList",nameList);
    }

}

