package com.online.edu.service.cms.controller.api;

import com.online.edu.common.base.result.R;
import com.online.edu.service.cms.entity.Ad;
import com.online.edu.service.cms.service.AdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin //解决跨域问题
@Api(tags = "广告推荐")
@RestController
@RequestMapping("/api/cms/ad")
public class ApiAdController {

    @Autowired
    private AdService adService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 这里查询的是 根据推荐位（广告的类型）来查询所有该类型的广告
    @ApiOperation("根据推荐位id显示广告推荐")
    @GetMapping("list/{adTypeId}")
    public R listByAdTypeId(@ApiParam(value = "推荐位id", required = true) @PathVariable String adTypeId){

       List<Ad> ads= adService.selectByAdTypeId(adTypeId);
       return R.ok().data("items", ads);
    }

    // 数据存入redis中必须进行序列化 对象必须完成 serialize序列化 ，实体类必须实现序列化接口
    @PostMapping("redis-saveTest")
    public R saveAd(@RequestBody Ad ad){

        redisTemplate.opsForValue().set("ad",ad);
        return R.ok();
    }
    @GetMapping("get-test/{key}")
    public R getAd(@PathVariable String key){
        Ad ad = (Ad)redisTemplate.opsForValue().get(key);
        return R.ok().data("ad", ad);
    }
    @DeleteMapping("remove-test/{key}")
    public R removeAd(@PathVariable String key){
        Boolean delete = redisTemplate.delete(key);
        System.out.println(delete);//是否删除成功
        Boolean hasKey = redisTemplate.hasKey(key);
        System.out.println(hasKey);//key是否存在
        return R.ok();
    }

}
