package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentController implements ContentControllerApiSpec {

    private final ContentService contentService;
}
