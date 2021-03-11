package com.online.edu.service.ucenter.mapper;

import com.online.edu.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author Answer
 * @since 2021-02-26
 */
@Repository
public interface MemberMapper extends BaseMapper<Member> {

    // 查询日期注册人数
    Integer selectRegisterNumByDay(String day);

}
