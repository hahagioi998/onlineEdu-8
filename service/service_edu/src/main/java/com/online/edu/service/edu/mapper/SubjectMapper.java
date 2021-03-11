package com.online.edu.service.edu.mapper;

import com.online.edu.service.edu.entity.Subject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.edu.service.edu.entity.vo.SubjectVo;

import java.util.List;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface SubjectMapper extends BaseMapper<Subject> {
    // 查询数据库中课程列表  根据ID查询数据
    List<SubjectVo> selectNestedListByParentId(String parentId);
}
