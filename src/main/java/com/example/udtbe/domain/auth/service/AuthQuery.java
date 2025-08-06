package com.example.udtbe.domain.auth.service;

import com.example.udtbe.domain.admin.entity.Admin;
import com.example.udtbe.domain.admin.exception.AdminErrorCode;
import com.example.udtbe.domain.admin.repository.AdminRepository;
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
    private final AdminRepository adminRepository;

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

    public void deleteAll() {
        memberRepository.deleteAll();
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RestApiException(AdminErrorCode.ADMIN_NOT_FOUND));
    }
}
