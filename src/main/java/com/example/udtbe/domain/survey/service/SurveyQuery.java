package com.example.udtbe.domain.survey.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyQuery {

    private final SurveyRepository surveyRepository;

    public boolean existsByMember(Member member) {
        return surveyRepository.existsByMember(member);
    }

    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }
}
