package com.online.edu.service.edu.controller.admin;


import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.ExceptionUtils;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.edu.entity.vo.SubjectVo;
import com.online.edu.service.edu.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
//@CrossOrigin  //允许跨域访问
@Api( tags = "课程分类管理")
@RestController
@Slf4j
@RequestMapping("/admin/edu/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @ApiOperation(value = "Excel批量导入课程类别数据")
    @PostMapping("import")
    public R batchImport(
            @ApiParam(value = "Excel文件", required = true)
            @RequestParam("file")MultipartFile file){

        try {
            InputStream inputStream = file.getInputStream();
            subjectService.batchImport(inputStream);
            return R.ok().message("批量导入成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new EduException(ResultCodeEnum.EXCEL_DATA_IMPORT_ERROR);
        }
    }

    // 将数据库中课程一级二级标题 嵌套的形式显示在前端
    @ApiOperation(value = "嵌套数据列表")
    @GetMapping("nested-list")
    public R nestedList(){
        List<SubjectVo> subjectVoList = subjectService.nestedList();
        return R.ok().data("items", subjectVoList);
    }


}

