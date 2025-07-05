package com.example.udtbe.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.member.repository.MemberRepository;

@DisplayName("[MemberController] 통합테스트")
class MemberControllerTest extends ApiSupport {

	@Autowired
	MemberRepository memberRepository;

}
