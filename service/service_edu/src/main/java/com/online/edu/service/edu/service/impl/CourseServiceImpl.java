package com.online.edu.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.common.base.result.R;
import com.online.edu.service.base.dto.CourseDto;
import com.online.edu.service.edu.entity.*;
import com.online.edu.service.edu.entity.form.CourseInfoForm;
import com.online.edu.service.edu.entity.vo.*;
import com.online.edu.service.edu.feign.OssFileService;
import com.online.edu.service.edu.mapper.*;
import com.online.edu.service.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    // 引入openfeign 才能够进行远程跨微服务调用方法
    @Autowired
    private OssFileService ossFileService;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CourseCollectMapper courseCollectMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveCurseInfo(CourseInfoForm courseInfoForm) {
        //保存课程基本信息 添加信息到数据库表edu_course
        Course course = new Course();
        //方法一： 可以这样 一个一个填充数据
        //course.setTitle(courseInfoForm.getTitle());
        //方法二：也可以使用 BeanUtils.copyProperties(courseInfoForm,course) 来完成属性的复制,两个参数一个源对象，一个目标对象
        // BeanUtils.copyProperties(courseInfoForm,course) 拷贝复制的时候，会把同名的对象属性进行拷贝。我们在设置的时候属性名称要相同
        BeanUtils.copyProperties(courseInfoForm,course);
        course.setStatus(Course.COURSE_DRAFT);   //注意静态变量要用类名引用，这个时候填写信息完成，还没有进行发布
        baseMapper.insert(course);

        //保存课程详情信息 添加信息到数据库表 edu_course_description
        CourseDescription courseDescription = new CourseDescription();
        //这里只有一个Description，这样更简便。
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());

        //注意：这里存储数据，不能够使用baseMapper了。因为ServiceImpl<CourseMapper, Course>。
        //原因： public interface CourseMapper extends BaseMapper<Course>。
        courseDescriptionMapper.insert(courseDescription);

        // 返回课程信息的ID值，以便前端后续的使用
        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        // 根据ID 从course表中取数据
        Course course = baseMapper.selectById(id);
        if(course == null){
            return null;
        }
        //从course_description表中取数据
        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);

        //创建courseInfoForm对象
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        courseInfoForm.setDescription(courseDescription.getDescription());

        return courseInfoForm;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        //保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    /**
     * 使用MyBatis Plus的分页插件和QueryWrapper结合自定义mapper xml实现多表关联查询
     * SELECT c.id,c.title,c.lesson_num as lessonNum,c.price,c.cover,c.buy_count as buyCount,c.view_count as viewCount,c.status,c.gmt_create as gmtCreate ,t.name as teacherName,
     * s1.title AS subjectParentTitle,s2.title AS subjectTitle
     * FROM edu_course c
     * left JOIN edu_teacher t on c.teacher_id = t.id
     * left JOIN edu_subject s1 on c.subject_parent_id=s1.id
     * left JOIN edu_subject s2 on c.subject_id = s2.id
     * WHERE
     * ORDER BY
     * LIMIT
     *
     * @param page
     * @param limit
     * @param courseQueryVo
     * @return
     */
    @Override
    public IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo) {
        /**
         * 组装多表联查的查询条件
         */
        // mybatis查询使用queryWrapper，返回CourseVo对象数据
        QueryWrapper<CourseVo> queryWrapper = new QueryWrapper<>();
        // 注意：这里column使数据库里要查询的字段，如果使用多表联查需要带上表别名，避免出现错误，表达不清楚
        queryWrapper.orderByDesc("c.gmt_create");

        // 获取组装查询条件  从courseQueryVo中获取 教师ID，一级分类，二级分类，课程名称
        String teacherId = courseQueryVo.getTeacherId();
        String subjectId = courseQueryVo.getSubjectId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String title = courseQueryVo.getTitle();

        if(!StringUtils.isEmpty(title)){
            // 注意：使用like会触发全能索引，降低查询效率，这里多表联查会使索引失效
            queryWrapper.like("c.title",title);
        }
        if(!StringUtils.isEmpty(teacherId)){
            queryWrapper.eq("c.teacher_id",teacherId);
        }
        if(!StringUtils.isEmpty(subjectParentId)){
            queryWrapper.eq("c.subject_parent_id",subjectParentId);
        }
        if(!StringUtils.isEmpty(subjectId)){
            queryWrapper.eq("c.subject_id",subjectId);
        }

        /**
         * 组装分页
         * 注意：若是多表联动，我们自己写的xml文件。我们选哟自定义完成分页
         */
        //创建page对象   mybatis-plus自带的page类  Page<CourseVo> 类型是我们需要组装的实体类
        Page<CourseVo> pageParam = new Page<>(page, limit);

        //执行查询 放入分页参数和查询条件参数，mp会自动组装 注意：若想进行分页查询，必须将分页参数放在方法的第一个参数中
        // 只要baseMapper检测到方法中第一个参数为page对象，就会在sql语句最后加上limit
        //只需要在mapper层传入封装好的分页组件即可，sq1分 页条件组装的过程由mp自动完成
        List<CourseVo> records = baseMapper.selectPageByCourseQueryVo(pageParam,queryWrapper);

        // 将结果数据列表设置到分页中就好，注意这里数据结果列表需要已经分页好的
        pageParam.setRecords(records);
        return pageParam;
    }

    // 从阿里云删除课程封面
    @Override
    public boolean removeAvatarById(String id) {
        // 1:根据ID先获取课程封面地址
        Course course = baseMapper.selectById(id);
        if(course != null){
            // 获取封面地址
        String cover = course.getCover();
        //有的讲师没有上传头像，所以进行判断讲师头像是否为空
        if(!StringUtils.isEmpty(cover)){
            // 返回一个R对象 对象里有是否成功
            R r = ossFileService.removeFile(cover);
            // 返回是否成功的状态
            return r.getSuccess();
        }
    }
        return false;
    }

    /**
     * 数据库外键约束的设置：
     *   根据阿里的开发手册，在互联网分布式项目中不得使用外键约束与级联更新。一切设计级联的操作都必须在业务层解决，不能依赖数据库
     *   只有单机低并发情况下才可以使用外键约束。
     * 业务层解决级联删除更新的方法：
     *    删除数据的时候，要先删除子表数据，在删除附表数据。
     *
     * @Transactional  默认情况下，数据库处于自动提交模式。每一条语句处于一个单独的事务中，在这条语句执行完毕时，如果执行成功则隐式的提交事务，如果
     * 执行失败则隐式的回滚事务。
     * @param id
     * @return
     */
    // 删除课程信息 注意：这是由多个业务共同实现的功能我们需要加事务来保证业务功能的一致性
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {

        // 关联多表删除时，先删除最底层的数据，由下往上，先删除叶子，在删除儿子
        //根据courseId删除video(课时) 这里的泛型时返回得到数据的类型
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id",id);
        // 删除课时信息 mapper层删除用delete
        videoMapper.delete(videoQueryWrapper);

        //根据courseId删除chapter(课程章节)
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id",id);
        chapterMapper.delete(chapterQueryWrapper);

        //根据courseId删除CourseDescription(课程描述)
        QueryWrapper<CourseDescription> courseDescriptionQueryWrapper = new QueryWrapper<>();
        courseDescriptionQueryWrapper.eq("id",id);
        courseDescriptionMapper.delete(courseDescriptionQueryWrapper);
        //注意：CourseDescription这里的courseId外键就是这个表的主键id
        //courseDescriptionMapper.deleteById(id);

        //根据courseId删除Comment(评论)
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id",id);
        commentMapper.delete(commentQueryWrapper);

        //根据courseId删除CourseCollect(课程关注收藏)
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq("course_id",id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        // 在自己实现类中，不需要引用使用baseMapper 返回的是影响的行数，通过判断是否大于等于1来查看是否完成删除
//        int i = baseMapper.deleteById(id);
//        if(i>=1){
//            return true;
//        }else {
//            return false;
//        }

        //删除课程  这里我们可以用service的自定义的删除方法
        boolean b = this.removeById(id);
        // 返回boolean值
        return b;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchRemoveByIds(List<String> idList) {
        // 设置返回值默认为false
        boolean b = false;
        //判断参数是否为空
        if(idList == null){
            return false;
        }
        // 删除数据
        for (String id : idList) {
            removeAvatarById(id);
           b = removeCourseById(id);
        }
        return b;
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        // 多表查询 通过课程ID获取数据
        return  baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {

        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        boolean b = this.updateById(course);
        return b;
    }

    @Override
    public List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        // 注意这里查询的必须是已经发布的课程
        queryWrapper.eq("status", Course.COURSE_NORMAL);

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectParentId())) {
            queryWrapper.eq("subject_parent_id", webCourseQueryVo.getSubjectParentId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectId())) {
            queryWrapper.eq("subject_id", webCourseQueryVo.getSubjectId());
        }

        // 只要有最热 最新 价格等数据传递过来 就进行排序（在前端设计 三个按钮不能同时按下）
        // 根据销售数量显示最热门课程  倒序排序
        if (!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())) {
            queryWrapper.orderByDesc("buy_count");
        }
        // 根据课程创建时间显示最新课程 倒叙排序
        if (!StringUtils.isEmpty(webCourseQueryVo.getGmtCreateSort())) {
            queryWrapper.orderByDesc("gmt_create");
        }
        // 根据价格显示课程
        if (!StringUtils.isEmpty(webCourseQueryVo.getPriceSort())) {
            // 在判断排序的类型
            if(webCourseQueryVo.getType() == null || webCourseQueryVo.getType() == 1){
                //再次点击 type=1转为正序
                queryWrapper.orderByAsc("price");
            }else{
                //点击价格后就会直接为直接为倒叙排列 type=2
                queryWrapper.orderByDesc("price");
            }
        }
        // 返回查询结果
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 获取课程信息并更新浏览量
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        //更新课程浏览数
        Course course = baseMapper.selectById(id);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        //获取课程信息
        return baseMapper.selectWebCourseVoById(id);
    }

    @Cacheable(value = "index", key = "'selectHotCourse'")
    @Override
    public List<Course> selectHotCourse() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        // 热门，人气从高到底排
        queryWrapper.orderByDesc("view_count");
        // 在SQL语句最后加上一条语句 limit 8
        queryWrapper.last("limit 8");
        return baseMapper.selectList(queryWrapper);
    }

//    @Override
//    public CourseDto getCourseDtoById(String courseId) {
//        // 我们可以分别获取数据，再进行数据组装 注意：这种方式将实现放在了业务层，但需要执行两次sql语句
//        Course course = baseMapper.selectById(courseId);
//        String teacherId = course.getTeacherId();
//        Teacher teacher = teacherMapper.selectById(teacherId);
//        CourseDto courseDto = new CourseDto();
//        courseDto.setId(course.getId());
//        courseDto.setTitle(course.getTitle());
//        courseDto.setCover(course.getCover());
//        courseDto.setPrice(course.getPrice());
//        courseDto.setTeacherName(teacher.getName());
//        return courseDto;
//    }


    @Override
    public CourseDto getCourseDtoById(String courseId) {
        // 这种方式把功能的实现，数据的组装放在了数据访问层，通过关联查询表执行一次sql语句完成功能
        return baseMapper.selectCourseDtoById(courseId);
    }

    @Override
    public void updateBuyCountById(String id) {
        Course course = baseMapper.selectById(id);
        course.setBuyCount(course.getBuyCount() + 1);
        this.updateById(course);
    }
}
