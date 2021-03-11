package com.online.edu.service.oss.controller.admin;

import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.ExceptionUtils;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Api(tags = "阿里云文件管理")
//@CrossOrigin
@RestController
@RequestMapping("/admin/oss/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("文件上传-头像/课程图片上传")
    @PostMapping("upload")
    public R upload(
            @ApiParam(value = "文件",required =  true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "模块",required = true)
            @RequestParam("module") String module)  {

        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String uploadUrl = fileService.upload(inputStream, module, originalFilename);

            return R.ok().message("文件上传成功").data("url",uploadUrl);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            // 这里 捕获的是Exception，不能是具体的异常，抛出的是我们自定义的异常
            // 我们抛出的异常也可能不存在，所以应该也先进行捕获 但是Exception类是检查类异常 只能抛出或者捕获，自定义异常应该继承运行时异常
            throw new EduException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
    }

    //删除阿里云中的图片
    @ApiOperation("文件删除")
    @DeleteMapping("remove")
    public R removeFile(
            @ApiParam(value = "要删除的文件路径", required = true) @RequestBody String url) {

        fileService.removeFile(url);
        return R.ok().message("文件刪除成功");
    }

}
