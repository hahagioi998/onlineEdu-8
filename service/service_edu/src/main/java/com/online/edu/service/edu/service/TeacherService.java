package com.online.edu.service.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.service.edu.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.edu.entity.vo.TeacherQueryVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface TeacherService extends IService<Teacher> {

    //自定义的分页查询  根据数据查询对象  返回的数据分页显示
    IPage<Teacher> selectPage(Page<Teacher> teacherPage, TeacherQueryVo teacherQueryVo);
    // 根据关键词查询讲师名列表
    List<Map<String, Object>> selectNameList(String key);

    //根据讲师id删除阿里云图片
    boolean removeAvatarById(String id);
   // 根据讲师ID 来获取讲师的简介信息，包括了课程信息，教师信息
    Map<String,Object> selectTeacherInfoById(String id);
    //获取首页推荐前4条讲师数据
    List<Teacher> selectHotTeacher();
}
