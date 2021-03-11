package com.online.edu.service.statistics.service;

import com.online.edu.service.statistics.entity.Daily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author Answer
 * @since 2021-03-05
 */
public interface DailyService extends IService<Daily> {


    // 创建统计数据进行存储
    void createStatisticsByDay(String day);

    // 显示统计数据
    Map<String, Map<String, Object>> getChartData(String begin, String end);

}
