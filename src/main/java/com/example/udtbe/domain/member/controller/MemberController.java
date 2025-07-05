package com.example.udtbe.domain.member.controller;

import com.example.udtbe.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerApiSpec {

    private final MemberService memberService;


}
