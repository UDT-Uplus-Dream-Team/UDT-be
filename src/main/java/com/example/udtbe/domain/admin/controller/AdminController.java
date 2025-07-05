package com.example.udtbe.domain.admin.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.udtbe.domain.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApiSpec {

	private final AdminService adminService;
}
