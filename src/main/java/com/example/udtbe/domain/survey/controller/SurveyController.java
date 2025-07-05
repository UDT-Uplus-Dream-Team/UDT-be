package com.example.udtbe.domain.survey.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.udtbe.domain.survey.service.SurveyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SurveyController implements SurveyControllerApiSpec {

	private final SurveyService surveyService;
}
