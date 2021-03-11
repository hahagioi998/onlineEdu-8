package com.online.edu.service.vod.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;

import java.io.InputStream;
import java.util.List;

public interface VideoService {

    // 视频上传方法  返回一个视频ID 由此判断是否上传成功
    String uploadVideo(InputStream inputStream, String originalFilename);
    // 根据阿里云上传视频的ID 删除阿里云上的视频
    public void  removeVideo(String videoId) throws ClientException;

    // 根据阿里云视频ID数组 批量删除视频（注意最多一次性删除20个）
    void removeVideoByIdList(List<String> videoIdList) throws ClientException;
    // 根据阿里云视频ID 获取视频播放凭证
    String getPlayAuth(String videoSourceId) throws ClientException;
}
