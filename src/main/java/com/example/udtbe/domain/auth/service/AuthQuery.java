package com.example.udtbe.domain.auth.service;

import org.springframework.stereotype.Component;

import com.example.udtbe.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthQuery {

	private final MemberRepository memberRepository;
}
