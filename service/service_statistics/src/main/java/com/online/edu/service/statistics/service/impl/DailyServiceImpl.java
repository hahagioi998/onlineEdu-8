package com.online.edu.service.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.edu.common.base.result.R;
import com.online.edu.service.statistics.entity.Daily;
import com.online.edu.service.statistics.feign.UcenterMemberService;
import com.online.edu.service.statistics.mapper.DailyMapper;
import com.online.edu.service.statistics.service.DailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2021-03-05
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {

    @Autowired
    private UcenterMemberService ucenterMemberService;

    @Override
    public void createStatisticsByDay(String day) {

        //如果当日的统计记录已存在，则删除重新统计|或 提示用户当日记录已存在
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_calculated", day);
        baseMapper.delete(queryWrapper);

        //远程获取注册用户数的统计结果
        R num = ucenterMemberService.countRegisterNum(day);
        Integer registerNum = (Integer)num.getData().get("registerNum");
        // TODO 注意：这些事随机生成的 可以再写接口完成真正的实现
        int loginNum = RandomUtils.nextInt(100, 200);   // 登陆数  可以给数据库表价格登陆时间字段，初始化为和创建时间，每次登陆修改为登陆的当前时间
        int videoViewNum = RandomUtils.nextInt(100, 200); // 新增视频数
        int courseNum = RandomUtils.nextInt(100, 200);  // 新增课程数

        //在本地数据库创建统计信息
        Daily daily = new Daily();
        daily.setRegisterNum(registerNum);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(day);

        baseMapper.insert(daily);
    }

    @Override
    public Map<String, Map<String, Object>> getChartData(String begin, String end) {

        //用户登陆数统计
        Map<String, Object> registerNum = this.getChartDataByType(begin, end, "register_num");
        //用户注册数统计
        Map<String, Object> loginNum = this.getChartDataByType(begin, end, "login_num");
        //新增课程视频播放数统计
        Map<String, Object> videoViewNum = this.getChartDataByType(begin, end, "video_view_num");
        //新增课程数统计
        Map<String, Object> courseNum = this.getChartDataByType(begin, end, "course_num");

        Map<String, Map<String, Object>> map = new HashMap<>();
        map.put("registerNum", registerNum);
        map.put("loginNum", loginNum);
        map.put("videoViewNum", videoViewNum);
        map.put("courseNum", courseNum);
        return map;
    }

    /**
     * 根据时间和要查询的列 查询数据
     * @param begin
     * @param end
     * @param type  要查询的列明
     * @return
     */
    private Map<String, Object> getChartDataByType(String begin, String end, String type){

        HashMap<String, Object> map = new HashMap<>();

        ArrayList<String> xList = new ArrayList<>();//日期列表
        ArrayList<Integer> yList = new ArrayList<>();//数据列表
        // 查询数据
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        // select:你要查询的列数 date_calculated和type
        queryWrapper.select("date_calculated",type);
        queryWrapper.between("date_calculated", begin, end);
        // baseMapper.selectMaps(queryWrapper); 查询结果以map形式返回
        List<Map<String, Object>> mapsData = baseMapper.selectMaps(queryWrapper);
        for (Map<String, Object> data : mapsData) {

            String dateCalculated = (String)data.get("date_calculated");
            xList.add(dateCalculated);

            Integer count = (Integer) data.get(type);
            yList.add(count);
        }

        map.put("xData", xList);
        map.put("yData", yList);
        return map;
    }
}
