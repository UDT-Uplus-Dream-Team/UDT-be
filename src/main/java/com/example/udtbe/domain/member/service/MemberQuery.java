package com.example.udtbe.domain.member.service;

import com.example.udtbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberQuery {

    private final MemberRepository memberRepository;
}
