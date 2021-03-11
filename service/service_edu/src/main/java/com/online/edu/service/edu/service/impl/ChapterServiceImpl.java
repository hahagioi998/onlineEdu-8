package com.online.edu.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.edu.service.edu.entity.Chapter;
import com.online.edu.service.edu.entity.Video;
import com.online.edu.service.edu.entity.vo.ChapterVo;
import com.online.edu.service.edu.entity.vo.VideoVo;
import com.online.edu.service.edu.mapper.ChapterMapper;
import com.online.edu.service.edu.mapper.VideoMapper;
import com.online.edu.service.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeChapterById(String id) {


        //课时信息：video
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("chapter_id", id);
        videoMapper.delete(videoQueryWrapper);

        //章节信息：chapter
        return this.removeById(id);
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {

        // 返回值是章节列表List<ChapterVo>，进行组装
        // 获取章节信息.注意：这里查询的不是主键ID，所以不能使用baseMapper.selectById()
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        queryWrapper.orderByAsc("sort","id");
        List<Chapter> chapterList = baseMapper.selectList(queryWrapper);

        //获取课时列表信息
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id",courseId);
        videoQueryWrapper.orderByAsc("sort","id");
        List<Video> videoList = videoMapper.selectList(videoQueryWrapper);

        //组装数据List<ChapterVo>
        ArrayList<ChapterVo> chapterVoList = new ArrayList<>();

        //方案一：拿到mapper数据访问层解决，效率低  1+n个sql 注意：sql的循环更加低效率
        //先根据课程ID 获取List<Chapter> sql  1
        // 进行遍历List<Chapter>，通过遍历到的章节ID获取章节下的video信息 List<Video> sql n
//        for (Chapter chapter : chapterList) {
//            ChapterVo chapterVo = new ChapterVo();
//            BeanUtils.copyProperties(chapter,chapterVo);
//            chapterVoList.add(chapterVo);
//            String chapterId = chapter.getId();
//            // 根据这个主键id获取video。即得到这个章节下的所有视频
//            //这是1+n 了
//            QueryWrapper<Video> videoQueryWrappers = new QueryWrapper<>();
//            videoQueryWrappers.eq("chapter_id",chapterId);
//            videoQueryWrappers.orderByAsc("sort","id");
//            List<Video> videos = videoMapper.selectList(videoQueryWrappers);
//        }

        //方案二：拿到service业务层解决 1+1个sql
        // 通过课程id获取章节列表信息 List<Chapter> sql 1
        // 通过课程id直接获取List<Video> sql 1
        // 获取这个chapter的主键ID

        //得到获取List<Chapter>后,进行遍历,得到的Chapter数据组装chapterVo，然后再把数据封装到chapterVos中
        for (Chapter chapter : chapterList) {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter,chapterVo);
            //组装videoVo
            List<VideoVo> videoVoList = new ArrayList<>();
            // 遍历videoList判断这个Video的chapter_id与chapter的id相同
            for (Video video : videoList) {
                // 进行判断
                if(chapter.getId().equals(video.getChapterId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video,videoVo);
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
            chapterVoList.add(chapterVo);
        }
        return chapterVoList;
    }
}
