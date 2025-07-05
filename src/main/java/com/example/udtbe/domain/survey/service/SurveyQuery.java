package com.example.udtbe.domain.survey.service;

import com.example.udtbe.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyQuery {

    private final SurveyRepository surveyRepository;
}
