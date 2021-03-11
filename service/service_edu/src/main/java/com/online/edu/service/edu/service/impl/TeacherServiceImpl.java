package com.online.edu.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.entity.Course;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.entity.vo.TeacherQueryVo;
import com.online.edu.service.edu.feign.OssFileService;
import com.online.edu.service.edu.mapper.CourseMapper;
import com.online.edu.service.edu.mapper.TeacherMapper;
import com.online.edu.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    // 引入openfeign 才能够进行远程跨微服务调用方法
    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private CourseMapper courseMapper;

    //ServiceImpl里注入了baseMapper  baseMapper就是teacherMapper

    //自定义的分页查询  根据数据查询对象  返回的数据分页显示
    @Override
    public IPage<Teacher> selectPage(Page<Teacher> teacherPage, TeacherQueryVo teacherQueryVo) {

        //显示分页查询列表
//      1. 排序：按照数据库中的sort字段排序
        QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
        //ASC（从小/上到大/下）升序     DESC降序
        teacherQueryWrapper.orderByAsc("sort");
//      2.查询
        if(teacherQueryVo == null){
            return baseMapper.selectPage(teacherPage,teacherQueryWrapper);
        }
//      3.条件查询
        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String joinDateBegin = teacherQueryVo.getJoinDateBegin();
        String joinDateEnd = teacherQueryVo.getJoinDateEnd();

        if(!StringUtils.isEmpty(name)){
            teacherQueryWrapper.like("name",name);
        }

        if(level != null){
            teacherQueryWrapper.eq("level",level);
        }

        if(!StringUtils.isEmpty(joinDateBegin)){
            teacherQueryWrapper.ge("join_date",joinDateBegin);
        }

        if(!StringUtils.isEmpty(joinDateEnd)){
            teacherQueryWrapper.le("join_date",joinDateEnd);
        }
        /**
         * 注意：单表查询的分页显示，只能单表分页。不能是多表联查的分页。多表联查的我们需要自己定义
         *  我们这里的teacherPage是在视图层controller中创建的自带的page对象
         *  ：Page<Teacher> teacherPage = new Page<>(page, limit);
         *  这里调用的baseMapper.selectPage（）是 MyBatis-Plus为我们已经封装好的方法
         *  返回的就是一个分页数据。
         */
        return baseMapper.selectPage(teacherPage,teacherQueryWrapper);
    }

    // 根据关键词查询讲师名列表
    @Override
    public List<Map<String, Object>> selectNameList(String key) {

        QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
        teacherQueryWrapper.select("name");
        teacherQueryWrapper.like("name",key);
        // 这是一个list集合，里面有多个map （不是数据都存放在了一个map中）
        List<Map<String, Object>> list = baseMapper.selectMaps(teacherQueryWrapper);

        //更换map中key的值  在后端传递的参数名与前端要求 不一致时
//        for (Map<String, Object> map : list) {
//            //这里遍历的 不是同一个map  是不同的list  集合
//            //System.out.println(map.keySet()+"*****"+map.get("name"));
//            map.put("value",map.get("name"));
//            map.remove("name");
//        }

        return list;
    }

    //根据讲师id删除阿里云图片
    @Override
    public boolean removeAvatarById(String id) {
        //根据讲师ID获取讲师的avatar的url。从数据库中获取
        Teacher teacher = baseMapper.selectById(id);
        //先判断teacher 是不是等于null
        if(teacher != null){
            //获取讲师的avatar
            String avatar = teacher.getAvatar();
            //有的讲师没有上传头像，所以进行判断讲师头像是否为空
            if(!StringUtils.isEmpty(avatar)){
                // 返回一个R对象 对象里有是否成功
                R r = ossFileService.removeFile(avatar);
                // 返回是否成功的状态
                return r.getSuccess();
            }
        }
        return false;
    }

    //组装数据  TeacherInfo，ID是教师ID
    @Override
    public Map<String, Object> selectTeacherInfoById(String id) {

        // 获取教师信息
        Teacher teacher = baseMapper.selectById(id);
        // 根据讲师ID查询讲师教授的课程
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("teacher_id",id);
        // 在课程表中根据教师ID进行查询
        List<Course> courseList = courseMapper.selectList(courseQueryWrapper);

        Map<String, Object> map =new HashMap<>();
        map.put("teacher",teacher);
        map.put("courseList",courseList);

        return map;
    }

    @Cacheable(value = "index", key = "'selectHotTeacher'")
    @Override
    public List<Teacher> selectHotTeacher() {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        queryWrapper.last("limit 4");
        return baseMapper.selectList(queryWrapper);
    }
}
