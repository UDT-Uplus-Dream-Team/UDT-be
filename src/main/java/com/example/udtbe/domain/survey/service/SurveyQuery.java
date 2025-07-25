package com.example.udtbe.domain.survey.service;

import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.exception.SurveyErrorCode;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import com.example.udtbe.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyQuery {

    private final SurveyRepository surveyRepository;

    private final ContentMetadataRepository contentMetadataRepository;

    public boolean existsByMember(Member member) {
        return surveyRepository.existsByMember(member);
    }

    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }

    public Survey findSurveyByMemberId(Long memberId) {
        return surveyRepository.findByMemberId(memberId).orElseThrow(
                () -> new RestApiException(SurveyErrorCode.SURVEY_NOT_FOUND)
        );
    }

    public Long findContentMetadataId(Long contentId) {
        return contentMetadataRepository.findIdByContent_Id(contentId).orElseThrow(
                () -> new RestApiException(ContentErrorCode.CONTENT_METADATA_NOT_FOUND)
        );
    }
}
