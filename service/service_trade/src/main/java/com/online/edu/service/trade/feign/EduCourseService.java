package com.online.edu.service.trade.feign;

import com.online.edu.common.base.result.R;
import com.online.edu.service.base.dto.CourseDto;
import com.online.edu.service.trade.feign.fallback.EduCourseServiceFallBack;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(value = "service-edu",fallback = EduCourseServiceFallBack.class)
public interface EduCourseService {

    // 这个方法 返回结果不是给前端的 是给内部微服务的 可以直接返回对象
    @GetMapping("/api/edu/course/inner/get-course-dto/{courseId}")
    CourseDto getCourseDtoById(@PathVariable(value = "courseId") String courseId);

    // 根据课程ID 更新销售数量
    @GetMapping("/api/edu/course/inner/update-buy-count/{id}")
    R updateBuyCountById(@PathVariable("id") String id);

}
