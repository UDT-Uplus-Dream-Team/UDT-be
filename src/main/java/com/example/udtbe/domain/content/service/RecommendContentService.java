package com.example.udtbe.domain.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendContentService {

    private final ContentQuery contentQuery;
}
