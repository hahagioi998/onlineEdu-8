package com.online.edu.service.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.vod.service.VideoService;
import com.online.edu.service.vod.util.AliyunVodSDKUtils;
import com.online.edu.service.vod.util.VodProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VodProperties vodProperties;

    /**
     *  视频上传 返回VideoID
     * @param inputStream 文件上传的字节流
     * @param originalFilename  文件名（包括扩展名）
     * @return
     */
    // 视频上传
    @Override
    public String uploadVideo(InputStream inputStream, String originalFilename) {
        // 获取文件标题，即去掉拓展名的文件名
        String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        // 获取上传请求对象，并设置请求参数
        UploadStreamRequest request = new UploadStreamRequest(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret(),
                title, originalFilename, inputStream);

        /* 模板组ID(可选) 暂时关闭功能，需要钱*/
//        request.setTemplateGroupId(vodProperties.getTemplateGroupId());
        /* 工作流ID（可选）暂时关闭审核，需要钱*/
//        request.setWorkflowId(vodProperties.getWorkflowId());
         // 创建上传对象实现类uploader

        UploadVideoImpl uploader = new UploadVideoImpl();
        // 进行上传
        UploadStreamResponse response = uploader.uploadStream(request);

        String videoId = response.getVideoId();
        //没有正确的返回videoid则说明上传失败
        if(StringUtils.isEmpty(videoId)){
            log.error("阿里云上传失败：" + response.getCode() + " - " + response.getMessage());
            throw new EduException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }

    @Override
    public void removeVideo(String videoId) throws ClientException {
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());
        // 设置删除视频的请求request
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(videoId);
        // 客户端响应请求
        client.getAcsResponse(request);
    }

    @Override
    public void removeVideoByIdList(List<String> videoIdList) throws ClientException {

        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());
        // 设置删除视频的请求request
        DeleteVideoRequest request = new DeleteVideoRequest();
        //支持传入多个视频ID，多个用逗号分隔，注意：这里获取的是数组，我么需要进行字符拼接，用逗号隔开
        int size = videoIdList.size(); // 获取视频ID数组的长度
        StringBuffer idListStr = new StringBuffer();  // 拼接组装好的字符串
        // 快捷键：size.for
        for (int i = 0; i < size; i++) {
            // StringBuffer 拼接的方式是append
            idListStr.append(videoIdList.get(i));
            // 这里进行判断,是否上传完毕。且判断是否 超过20个id 因为一次性上传最多20个
            if(i == size - 1 || i%20 == 19){   //即：size=0,i=19，从0开始 即为20了。或者i=39，59，79时，这样也是每隔20删除一次
                log.info("idListStr = " +idListStr.toString());
                // 删除视频
                request.setVideoIds(idListStr.toString());
                client.getAcsResponse(request);
                //如果videoIdList > 20个，则我们在这里删除结束后清空idListStr，在重新组装
                idListStr = new StringBuffer(); // 对数组进行初始化，重新new一下
            }else  if(i % 20 < 19){
                //中间用逗号隔开 前19个都用,隔开
                idListStr.append(",");
            }
        }
    }

    @Override
    public String getPlayAuth(String videoSourceId) throws ClientException {
        //初始化client对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());

        //创建请求对象
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest ();
        request.setVideoId(videoSourceId);

        //获取响应
        GetVideoPlayAuthResponse response = client.getAcsResponse(request);

        return response.getPlayAuth();
    }
}
