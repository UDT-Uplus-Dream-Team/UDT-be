package com.example.udtbe.domain.member.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.udtbe.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerApiSpec {

	private final MemberService memberService;


}
