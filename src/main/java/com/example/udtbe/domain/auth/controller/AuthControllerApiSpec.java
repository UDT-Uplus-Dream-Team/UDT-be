package com.example.udtbe.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Auth API", description = "인증/인가 관련 API")
public interface AuthControllerApiSpec {

    @Operation(summary = "로그아웃 API", description = "로그아웃한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "토큰 재발급 API", description = "토큰을 재발급한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/api/auth/reissue/token")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response);
}