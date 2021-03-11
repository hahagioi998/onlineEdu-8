package com.online.edu.service.edu.feign;

import com.online.edu.common.base.result.R;
import com.online.edu.service.edu.feign.fallback.OssFileServiceFallBack;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

//远程调用 跨微服务调用方法
@Service
@FeignClient(value = "service-oss",fallback = OssFileServiceFallBack.class)
public interface OssFileService {

    @GetMapping("/admin/oss/file/test")
    R test();



    @DeleteMapping("/admin/oss/file/remove")
    R removeFile(@RequestBody String url);

}
