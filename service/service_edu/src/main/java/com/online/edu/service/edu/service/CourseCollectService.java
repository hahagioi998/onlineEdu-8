package com.online.edu.service.edu.service;

import com.online.edu.service.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.edu.entity.vo.CourseCollectVo;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface CourseCollectService extends IService<CourseCollect> {

    // 判断课程是否已被收藏
    boolean isCollect(String courseId, String memberId);

    // 收藏课程
    void saveCourseCollect(String courseId, String memberId);

    // 获取课程收藏列表
    List<CourseCollectVo> selectListByMemberId(String memberId);

    // 取消课程收藏
    boolean removeCourseCollect(String courseId, String memberId);

}
