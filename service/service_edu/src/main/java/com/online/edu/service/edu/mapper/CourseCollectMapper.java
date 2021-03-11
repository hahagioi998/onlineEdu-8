package com.online.edu.service.edu.mapper;

import com.online.edu.service.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.edu.service.edu.entity.vo.CourseCollectVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程收藏 Mapper 接口
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Repository
public interface CourseCollectMapper extends BaseMapper<CourseCollect> {

    // 获取课程收藏列表对象 多表联查
    List<CourseCollectVo> selectPageByMemberId(String memberId);
}
