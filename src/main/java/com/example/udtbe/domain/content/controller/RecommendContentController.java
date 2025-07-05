package com.example.udtbe.domain.content.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.udtbe.domain.content.service.RecommendContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecommendContentController implements RecommendContentControllerApiSpec {

	private final RecommendContentService recommendContentService;
}
