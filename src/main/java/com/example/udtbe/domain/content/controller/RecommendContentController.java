package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.service.RecommendContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendContentController implements RecommendContentControllerApiSpec {

    private final RecommendContentService recommendContentService;
}
