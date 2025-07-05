package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApiSpec {

    private final AdminService adminService;
}
