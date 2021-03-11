package com.online.edu.service.edu.service;

import com.online.edu.service.edu.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.edu.entity.vo.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author Answer
 * @since 2020-11-23
 */
public interface ChapterService extends IService<Chapter> {

    // 根据ID删除章节信息
    boolean removeChapterById(String id);
    // 返回课程下的章节集合对象，章节中嵌套这章节下的视频
    List<ChapterVo> nestedList(String courseId);

}
