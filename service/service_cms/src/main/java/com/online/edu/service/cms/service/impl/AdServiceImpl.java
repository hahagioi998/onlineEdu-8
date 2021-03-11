package com.online.edu.service.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.common.base.result.R;
import com.online.edu.service.cms.entity.Ad;
import com.online.edu.service.cms.entity.vo.AdVo;
import com.online.edu.service.cms.feign.OssFileService;
import com.online.edu.service.cms.mapper.AdMapper;
import com.online.edu.service.cms.service.AdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * <p>
 * 广告推荐 服务实现类
 * </p>
 *
 * @author Answer
 * @since 2021-02-20
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {


    @Autowired
    private OssFileService ossFileService;

    @Override
    public IPage<AdVo> selectPage(Long page, Long limit) {
        // 注意：这里的QueryWrapper<AdVo>  是需要查询的对象
        QueryWrapper<AdVo> queryWrapper = new QueryWrapper<>();
        // 这里对查询的数据进行排序，先对广告类型（广告位进行排序），再对排序等级排序
        queryWrapper.orderByAsc("a.type_id", "a.sort");

        Page<AdVo> pageParam = new Page<>(page,limit);

        List<AdVo> records =  baseMapper.selectPageByQueryWrapper(pageParam,queryWrapper);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public boolean removeAdImageById(String id) {
        // 根据ID 查询数据（注意：每个广告都有一个广告位的图片）我们需要根据ID先查询广告图片地址
        Ad ad = baseMapper.selectById(id);
        if(ad != null){
            String imageUrl = ad.getImageUrl();
            // 在判断广告图片地址是否为空
            if(!StringUtils.isEmpty(imageUrl)){
                // 删除图片
                R r = ossFileService.removeFile(imageUrl);
                return r.getSuccess();
            }
        }
        return false;
    }

    /**
     * 标注在方法上，对方法返回结果进行缓存。下次请求时，如果缓存存在，则直接读取缓存数据返回；
     * 如果缓存不存在，则执行方法，并把返回的结果存入缓存中。一般用在查询方法上。
     * @param adTypeId
     * @return
     */
    @Cacheable(value = "index",key ="'selectByAdTypeId'")
    @Override
    public List<Ad> selectByAdTypeId(String adTypeId) {
        QueryWrapper<Ad> adQueryWrapper = new QueryWrapper<>();
        adQueryWrapper.eq("type_id", adTypeId);
        adQueryWrapper.orderByAsc("sort", "id");
        List<Ad> adList = baseMapper.selectList(adQueryWrapper);
        return adList;
    }
}
