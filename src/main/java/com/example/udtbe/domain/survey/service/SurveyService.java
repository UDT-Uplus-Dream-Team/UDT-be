package com.example.udtbe.domain.survey.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyQuery surveyQuery;
}
