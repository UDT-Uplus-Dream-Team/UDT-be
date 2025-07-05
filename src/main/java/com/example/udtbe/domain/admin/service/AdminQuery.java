package com.example.udtbe.domain.admin.service;

import org.springframework.stereotype.Component;

import com.example.udtbe.domain.admin.repository.AdminRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminQuery {

	private final AdminRepository adminRepository;
	private final ContentRepository contentRepository;
}
