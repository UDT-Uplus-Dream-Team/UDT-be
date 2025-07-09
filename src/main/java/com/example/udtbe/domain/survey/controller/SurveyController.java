package com.example.udtbe.domain.survey.controller;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SurveyController implements SurveyControllerApiSpec {

    private final SurveyService surveyService;

    @Override
    public ResponseEntity<Void> survey(SurveyCreateRequest request, Member member) {
        surveyService.createSurvey(request, member);

        return ResponseEntity.noContent().build();
    }
}
