package com.example.udtbe.domain.survey.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.SurveyMapper;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.exception.SurveyErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyQuery surveyQuery;

    public void createSurvey(SurveyCreateRequest request, Member member) {
        if (surveyQuery.existsByMember(member)) {
            throw new RestApiException(SurveyErrorCode.SURVEY_ALREADY_EXISTS_FOR_MEMBER);
        }

        Survey survey = SurveyMapper.toEntity(request, member);
        surveyQuery.save(survey);

    }
}
