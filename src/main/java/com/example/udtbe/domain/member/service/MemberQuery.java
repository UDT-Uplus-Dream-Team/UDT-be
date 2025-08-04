package com.example.udtbe.domain.member.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.exception.MemberErrorCode;
import com.example.udtbe.domain.member.repository.MemberRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberQuery {

    private final MemberRepository memberRepository;

    public Member findMemberById(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
    
    public List<Member> findMembersForAdmin(String cursor, int size, String keyword) {
        return memberRepository.findMembersForAdmin(cursor, size, keyword);
    }
}
