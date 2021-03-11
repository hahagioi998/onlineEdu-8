package com.online.edu.service.statistics.task;

import com.online.edu.service.statistics.service.DailyService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTask {

    @Autowired
    private DailyService dailyService;

    /**
     * 测试   cron:表达式 http://cron.qqe2.com/在线生成
     */
//    @Scheduled(cron="0/3 * * * * *") // 每隔3秒执行一次
//    public void task1() {
//        log.info("task1 执行");
//    }

    /**
     * 每天凌晨1点执行定时任务
     */
    @Scheduled(cron = "0 0 1 * * ?") //注意只支持6位表达式
    public void taskGenarateStatisticsData() {
        //获取上一天的日期  今天一点生成昨天的信息
        String day = new DateTime().minusDays(1).toString("yyyy-MM-dd");
        // 调用方法
        dailyService.createStatisticsByDay(day);
        log.info("taskGenarateStatisticsData 统计完毕");
    }
}
