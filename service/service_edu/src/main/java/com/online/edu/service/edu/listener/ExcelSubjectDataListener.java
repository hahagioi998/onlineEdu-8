package com.online.edu.service.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.edu.service.edu.entity.Subject;
import com.online.edu.service.edu.entity.excel.ExcelSubjectData;
import com.online.edu.service.edu.mapper.SubjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSubjectDataListener extends AnalysisEventListener<ExcelSubjectData> {

    //注意：这里ExcelSubjectDataListener没有让spring容器接管。类对象不是由spring容器生成的。说new出来的，所以我们不能直接导入对象
    // 这里对象的subjectMapper 是个空对象，我们没有对它赋值
     private SubjectMapper subjectMapper;


    /**
     * 遍历每一行的记录
     * @param data
     * @param context
     */
    @Override
    public void invoke(ExcelSubjectData data, AnalysisContext context) {
        log.info("解析到一条记录: {}", data);
        //处理读取出来的数据  data数据可以通过get来获取里面具体某个数据的值  ExcelSubjectData(levelOneTitle=人工智能, levelTwoTitle=Python)
        String levelOneTitle = data.getLevelOneTitle();   //一级标题
        String levelTwoTitle = data.getLevelTwoTitle();   //二级标题
        log.info("levelOneTitle: {}", levelOneTitle);
        log.info("levelTwoTitle: {}", levelTwoTitle);
        //存储一级列表 判读 一级标题是否已存在
        Subject subjectLevelOne = this.getByTitle(levelOneTitle);
        String parentId = null;
        // 若一级标题 数据库中不存在
        if(subjectLevelOne == null){
            // 若不存在 就组装这个一级类别
            Subject subject = new Subject();
            subject.setTitle(levelOneTitle);
            subject.setParentId("0");
            //存入数据库
            subjectMapper.insert(subject);
            //存进去后 获取一级类别的id
            parentId = subject.getId();
        }else {
            parentId = subjectLevelOne.getId();
        }
        //存储二级列表
        //判断 二级列表是否重复
        Subject subByTitle = this.getSubByTitle(levelTwoTitle, parentId);
        if(subByTitle == null){
            //将二级分类存入数据库
            Subject subject = new Subject();
            subject.setTitle(levelTwoTitle);
            subject.setParentId(parentId);
            subjectMapper.insert(subject);
        }


    }

    /**
     *  所有数据解析完成了 都会来调用
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

    /**
     * 根据分类名称查询这个一级分类是否存在
     * @param title
     * @return
     */
    private Subject getByTitle(String title){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id","0");    //判断一级分类
        // 返回这个 查询结果：若一级分类已存在数据库则不进行插入。若未存在，就进行插入
        return subjectMapper.selectOne(queryWrapper);
    }

    /**
     * 根据分类名称和父id查询这个二级分类是否存在
     * @param title
     * @param parentId
     * @return
     */
    private Subject getSubByTitle(String title, String parentId){
        // 根据同一级类别和二级类别 同一个相同的才算相同
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id",parentId);    //判断二级分类
        return subjectMapper.selectOne(queryWrapper);
    }
}
