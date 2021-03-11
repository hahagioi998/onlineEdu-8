package com.online.edu.service.edu.feign;

import com.online.edu.service.base.dto.MemberCommentDto;
import com.online.edu.service.edu.feign.fallback.UcenterMemberServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(value = "service-ucenter", fallback = UcenterMemberServiceFallBack.class)
public interface UcenterMemberService {

    // 根据会员id查询会员信息 评论
    @GetMapping(value = "/api/ucenter/member/inner/get-memberCommen-dto/{memberId}")
    MemberCommentDto getMemberCommentDtoByMemberId(@PathVariable(value = "memberId") String memberId);
}
