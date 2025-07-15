package com.example.udtbe.domain.auth.controller;

import com.example.udtbe.domain.auth.dto.request.TempAuthRequest;
import com.example.udtbe.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApiSpec {

    private final AuthService authService;

    @Override
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        authService.reissue(request, response);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> tempSignUp(TempAuthRequest request) {
        authService.tempSignUp(request);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<Void> tempSignIn(TempAuthRequest request, HttpServletResponse response) {
        authService.tempSignIn(request, response);
        return ResponseEntity.noContent().build();
    }
}
