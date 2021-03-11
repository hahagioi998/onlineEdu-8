package com.online.edu.service.edu.controller.admin;


import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.entity.Chapter;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.entity.vo.ChapterVo;
import com.online.edu.service.edu.service.ChapterService;
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
@Api( tags = "课程章节管理")
@RestController
@Slf4j
@RequestMapping("/admin/edu/chapter")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;
    @Autowired
    private VideoService videoService;


    //新增讲师   注意：在restf风格中，post格式提交数据，对象类型用json方式提交 使用注解@RequestBody 即可
    @ApiOperation("新增讲师")
    @PostMapping("/save")
    public R save(@ApiParam("章节对象") @RequestBody Chapter chapter){
        //插入数据的时候要自动填充时间
        boolean result = chapterService.save(chapter);
        if(result){
            return R.ok().message("添加成功");
        }else{
            return R.error().message("添加失败");
        }
    }

    //根据id获取课程章节信息  @PathVariable将URL中占位符参数{xxx}绑定到处理器类的方法形参中@PathVariable(“xxx“)，@GetMapping("/get/{id}")
    @ApiOperation("根据id获取课程章节")
    @GetMapping("/get/{id}")
    public R getById(@ApiParam(value = "课程章节ID",required = true) @PathVariable String id ){
        Chapter chapter = chapterService.getById(id);
        if(chapter != null){
            return R.ok().data("item", chapter);
        }else{
            return R.error().message("数据不存在");
        }
    }

    //更新讲师信息
    @ApiOperation("更新课程章节信息")
    @PutMapping("/update")
    public R updateById(@ApiParam("编辑课程章节对象") @RequestBody Chapter chapter){
        boolean result = chapterService.updateById(chapter);
        if(result){
            return R.ok().message("修改成功");
        }else {
            return R.error().message("添加失败，该讲师不存在");
        }
    }

    @ApiOperation("根据ID删除章节")
    @DeleteMapping("remove/{id}")
    public R removeById(
            @ApiParam(value = "章节ID", required = true)
            @PathVariable String id){

        // 删除视频：VOD 视频存在章节中，删除章节前必须删除子表视频表
        //在此处调用vod中的删除视频文件的接口 删除多个视频
        videoService.removeMediaVideoByChapterId(id);

        // 注意这里是多表联查删除
        boolean result = chapterService.removeChapterById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }

    }

    @ApiOperation("嵌套章节数据列表")
    @GetMapping("nested-list/{courseId}")
    public R nestedListByCourseId(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId){
        // 根据课程ID查询课程下的章节信息，注意一个课程下会有许多章节，所以是返回的是集合类型。返回对象是ChapterVo
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("items", chapterVoList);
    }

}

