package com.online.edu.service.edu.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netflix.client.ClientException;
import com.online.edu.service.edu.entity.Chapter;
import com.online.edu.service.edu.entity.Video;
import com.online.edu.service.edu.feign.VodMediaService;
import com.online.edu.service.edu.mapper.VideoMapper;
import com.online.edu.service.edu.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    // 引入openfeign远程接口，调用里面的方法
    @Autowired
    private VodMediaService vodMediaService;

    // 根据视频ID 删除阿里云上存储的视频
    @Override
    public void removeMediaVideoById(String id) {
        // 打印日志 输出视频的ID: videoId
        log.warn("VideoServiceImpl：video id = " + id);
        //查询数据库 根据videoId找到视频id： video_source_id
        Video video = baseMapper.selectById(id);
        String videoSourceId = video.getVideoSourceId();
        // 打印日志 输出阿里云上视频的ID： video_source_id
        log.warn("VideoServiceImpl：videoSourceId= " + videoSourceId);
        // 调用远程接口删除 阿里云上的视频 这里是删除单个视频
        vodMediaService.removeVideo(videoSourceId);
    }

    // 注意：这里是根据章节ID，chapterId来查询视频ID，同表的videoSourceId
    @Override
    public void removeMediaVideoByChapterId(String chapterId) {
        // 拼装查询条件
        QueryWrapper<Video> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("chapter_id",chapterId);
        chapterQueryWrapper.select("video_source_id");
        List<Video> videoList = baseMapper.selectList(chapterQueryWrapper);

        List<String> videoSourceIdList = new ArrayList<>();
        for (Video video : videoList) {
            videoSourceIdList.add(video.getVideoSourceId());
        }
        // 调用远程接口删除 阿里云上的视频 这里是删除多个视频
        vodMediaService.removeVideoByIdList(videoSourceIdList);
    }

    @Override
    public void removeMediaVideoByCourseId(String courseId) {
        // 拼装查询条件
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("video_source_id");
        queryWrapper.eq("course_id",courseId);
        List<Video> videoList = baseMapper.selectList(queryWrapper);

        List<String> videoSourceIdList = new ArrayList<>();
        for (Video video : videoList) {
            videoSourceIdList.add(video.getVideoSourceId());
        }
        // 调用远程接口删除 阿里云上的视频 这里是删除多个视频
        vodMediaService.removeVideoByIdList(videoSourceIdList);
    }

}
