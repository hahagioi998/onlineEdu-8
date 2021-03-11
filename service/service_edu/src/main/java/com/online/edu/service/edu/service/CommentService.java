package com.online.edu.service.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.service.edu.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 评论 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface CommentService extends IService<Comment> {

    // 分页查询
    IPage<Comment> selectPage(Page<Comment> commentPage, String courseId);
}
