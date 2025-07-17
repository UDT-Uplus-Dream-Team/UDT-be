package com.example.udtbe.domain.survey.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.SurveyMapper;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.exception.SurveyErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.token.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyQuery surveyQuery;
    private final CookieUtil cookieUtil;

    @Transactional
    public void createSurvey(SurveyCreateRequest request, Member member,
            HttpServletResponse response) {
        if (surveyQuery.existsByMember(member)) {
            throw new RestApiException(SurveyErrorCode.SURVEY_ALREADY_EXISTS_FOR_MEMBER);
        }

        Survey survey = SurveyMapper.toEntity(request, member);
        surveyQuery.save(survey);

        member.updateRole(ROLE_USER);
        cookieUtil.deleteCookie(response);
    }
}
