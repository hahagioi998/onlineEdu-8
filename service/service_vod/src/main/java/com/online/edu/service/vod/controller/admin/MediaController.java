package com.online.edu.service.vod.controller.admin;

import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(tags="阿里云视频点播")
//@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/vod/media")
@Slf4j
public class MediaController {

    /**
     *   MediaController就是 视频点播技术的视频上传和删除工作
     */


    @Autowired
    private VideoService videoService;

    /**
     * 文件上传
     * @param file MultipartFile file 注意：文件上传MultipartFile：一般是用来接受前台传过来的文件
     * @return
     */
    @PostMapping("upload")
    public R uploadVideo(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file) {

        try {
            // 获取文件的输入流
            InputStream inputStream = file.getInputStream();
            // 获取上传文件名称
            String originalFilename = file.getOriginalFilename();
            // 进行视频上传
            String videoId = videoService.uploadVideo(inputStream, originalFilename);
            // 到这里代表上传成功，返回给前端videoId
            return R.ok().message("视频上传成功").data("videoId", videoId);
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new EduException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
    }

    // 删除单个视频 根据阿里云视频ID
    @DeleteMapping("remove/{vodId}")
    public R removeVideo(
            @ApiParam(value="阿里云视频id", required = true)
            @PathVariable String vodId){

        try {
            videoService.removeVideo(vodId);
            return R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new EduException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

    // 批量删除阿里云视频 根据阿里云视频ID
    @DeleteMapping("remove")
    public R removeVideoByIdList(
            @ApiParam(value = "阿里云视频id列表", required = true)
            @RequestBody List<String> videoIdList){

        try {
            videoService.removeVideoByIdList(videoIdList);
            return  R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new EduException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);


        }
    }

}
