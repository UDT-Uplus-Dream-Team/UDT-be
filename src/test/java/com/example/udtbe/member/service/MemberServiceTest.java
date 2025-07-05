package com.example.udtbe.member.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.udtbe.domain.member.service.MemberQuery;
import com.example.udtbe.domain.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberQuery memberQuery;

	@InjectMocks
	private MemberService memberService;
}
