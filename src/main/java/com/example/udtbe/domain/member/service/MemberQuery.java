package com.example.udtbe.domain.member.service;

import org.springframework.stereotype.Component;

import com.example.udtbe.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberQuery {

	private final MemberRepository memberRepository;
}
