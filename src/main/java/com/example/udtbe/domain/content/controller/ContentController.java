package com.example.udtbe.domain.content.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.udtbe.domain.content.service.ContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ContentController implements ContentControllerApiSpec {

	private final ContentService contentService;
}
