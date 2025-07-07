package com.example.udtbe.domain.auth.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.exception.MemberErrorCode;
import com.example.udtbe.domain.member.repository.MemberRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthQuery {

    private final MemberRepository memberRepository;

    public Optional<Member> getOptionalMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
