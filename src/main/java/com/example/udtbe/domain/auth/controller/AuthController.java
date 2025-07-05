package com.example.udtbe.domain.auth.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.udtbe.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApiSpec {

	private final AuthService authService;
}
