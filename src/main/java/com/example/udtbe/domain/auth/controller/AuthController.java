package com.example.udtbe.domain.auth.controller;

import com.example.udtbe.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApiSpec {

    private final AuthService authService;
}
