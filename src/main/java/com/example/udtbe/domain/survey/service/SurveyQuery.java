package com.example.udtbe.domain.survey.service;

import org.springframework.stereotype.Component;

import com.example.udtbe.domain.survey.repository.SurveyRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurveyQuery {
	
	private final SurveyRepository surveyRepository;
}
