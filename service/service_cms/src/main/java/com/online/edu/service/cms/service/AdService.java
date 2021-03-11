package com.online.edu.service.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.online.edu.service.cms.entity.Ad;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.edu.service.cms.entity.vo.AdVo;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务类
 * </p>
 *
 * @author Answer
 * @since 2021-02-20
 */
public interface AdService extends IService<Ad> {

    // 自己组织的分页查询数据 多表联查 需要自己组装数据 组装查询条件 自己写SQL语句
    IPage<AdVo> selectPage(Long page, Long limit);
    // 根据ID删除广告推荐
    boolean removeAdImageById(String id);
    // 根据广告推荐位（广告类型）查询该类型的所欲广告
    List<Ad> selectByAdTypeId(String adTypeId);
}
