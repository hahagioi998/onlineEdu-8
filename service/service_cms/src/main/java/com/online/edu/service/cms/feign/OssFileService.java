package com.online.edu.service.cms.feign;

import com.online.edu.common.base.result.R;
import com.online.edu.service.cms.feign.fallback.OssFileServiceFallBack;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
// 注意：这里添加@Service只是为了在没有fallback容错的时候，为了后面的注入使用 @Autowired   private OssFileService ossFileService;添加的
// 在我们fallback = OssFileServiceFallBack.class这里有了容错后，就可写可不写了，建议写上

@Service
@FeignClient(value = "service-oss", fallback = OssFileServiceFallBack.class)
public interface OssFileService {

    @DeleteMapping("/admin/oss/file/remove")
    R removeFile(@RequestBody String url);

}
