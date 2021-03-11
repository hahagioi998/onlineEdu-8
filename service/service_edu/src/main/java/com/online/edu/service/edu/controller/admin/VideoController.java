package com.online.edu.service.edu.controller.admin;


import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.entity.Chapter;
import com.online.edu.service.edu.entity.Video;
import com.online.edu.service.edu.feign.VodMediaService;
import com.online.edu.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
//@CrossOrigin  //允许跨域访问
@Api( tags = "课时管理")
@RestController
@Slf4j
@RequestMapping("/admin/edu/video")
public class VideoController {

    /**
     * 这个是与数据库中的video表 坐交互的 。
     */

    @Autowired
    private VideoService videoService;

    //新增课时   注意：在restf风格中，post格式提交数据，对象类型用json方式提交 使用注解@RequestBody 即可
    @ApiOperation("新增课时")
    @PostMapping("save")
    public R save(
            @ApiParam(value="课时对象", required = true)
            @RequestBody Video video){
        boolean result = videoService.save(video);
        if (result) {
            return R.ok().message("保存成功");
        } else {
            return R.error().message("保存失败");
        }
    }

    //更新讲师信息
    @ApiOperation("更新课时信息")
    @PutMapping("/update")
    public R updateById(@ApiParam("编辑课时对象") @RequestBody Video video){
        boolean result = videoService.updateById(video);
        if(result){
            return R.ok().message("修改成功");
        }else {
            return R.error().message("添加失败，该讲师不存在");
        }
    }

    @ApiOperation("根据ID删除课时")
    @DeleteMapping("remove/{id}")
    public R removeById(
            @ApiParam(value = "课时ID", required = true)
            @PathVariable String id){

        //在此处调用vod中的删除视频文件的接口
        //删除阿里云视频
        videoService.removeMediaVideoById(id);
        // 删除数据库数据
        boolean result = videoService.removeById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }



    @ApiOperation("根据id查询课时")
    @GetMapping("get/{id}")
    public R getById(
            @ApiParam(value="课时id", required = true)
            @PathVariable String id){

        Video video = videoService.getById(id);
        if (video != null) {
            return R.ok().data("item", video);
        } else {
            return R.error().message("数据不存在");
        }
    }

}

