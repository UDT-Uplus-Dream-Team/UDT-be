package com.example.udtbe.domain.content.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendContentService {

	private final ContentQuery contentQuery;
}
