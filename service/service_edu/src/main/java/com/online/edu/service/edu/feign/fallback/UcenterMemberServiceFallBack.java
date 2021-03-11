package com.online.edu.service.edu.feign.fallback;

import com.online.edu.service.base.dto.MemberCommentDto;
import com.online.edu.service.base.dto.MemberDto;
import com.online.edu.service.edu.feign.UcenterMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UcenterMemberServiceFallBack implements UcenterMemberService {

    @Override
    public MemberCommentDto getMemberCommentDtoByMemberId(String memberId) {
        log.info("熔断保护");
        return null;
    }
}