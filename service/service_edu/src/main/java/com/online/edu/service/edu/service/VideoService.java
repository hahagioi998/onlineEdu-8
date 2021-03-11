package com.online.edu.service.edu.service;

import com.netflix.client.ClientException;
import com.online.edu.service.edu.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface VideoService extends IService<Video> {
    // 根据videoID删除视频ID 调用openfeign远程接口
    void removeMediaVideoById(String id);
    // 根据chapterId 删除多个视频和数据库中的数据
    void removeMediaVideoByChapterId(String chapterId);
    // courseId 删除多个视频和数据库中的数据
    void removeMediaVideoByCourseId(String courseId);

}
