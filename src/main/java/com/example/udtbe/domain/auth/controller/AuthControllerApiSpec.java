package com.example.udtbe.domain.auth.controller;

import com.example.udtbe.domain.auth.dto.request.TempAuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Operation(summary = "임시 회원가입(토큰 발급) API", description = "임시 회원가입 후 토큰을 발급한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/api/auth/temp-signup")
    public ResponseEntity<Void> tempSignUp(@RequestBody @Valid TempAuthRequest request);

    @Operation(summary = "임시 로그인(토큰 발급) API", description = "임시 로그인 후 토큰을 발급한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/api/auth/temp-signin")
    public ResponseEntity<Void> tempSignIn(
            @RequestBody @Valid TempAuthRequest request,
            HttpServletResponse response);
}