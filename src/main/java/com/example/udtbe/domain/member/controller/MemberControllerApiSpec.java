package com.example.udtbe.domain.member.controller;

import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Member API", description = "회원 관련 API")
@RequestMapping("/api")
public interface MemberControllerApiSpec {

    @Operation(summary = "마이페이지에서 유저 정보 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/users/me")
    ResponseEntity<MemberInfoResponse> getMemberInfo(
            @AuthenticationPrincipal Member member
    );
}
