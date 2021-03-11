package com.online.edu.service.edu.service;

import com.online.edu.service.edu.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.edu.entity.vo.SubjectVo;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface SubjectService extends IService<Subject> {

    //批量导入课程（课程分类）
    void batchImport(InputStream inputStream);

    //查询  课程标题嵌套数据列表
    List<SubjectVo> nestedList();

}
