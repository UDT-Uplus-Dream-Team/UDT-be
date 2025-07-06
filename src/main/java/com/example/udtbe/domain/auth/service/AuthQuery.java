package com.example.udtbe.domain.auth.service;

import com.example.udtbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthQuery {

    private final MemberRepository memberRepository;
}
