package com.example.udtbe.domain.survey.controller;

import com.example.udtbe.domain.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SurveyController implements SurveyControllerApiSpec {

    private final SurveyService surveyService;
}
