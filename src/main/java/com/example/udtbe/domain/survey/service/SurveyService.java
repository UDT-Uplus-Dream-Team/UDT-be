package com.example.udtbe.domain.survey.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;

import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.SurveyMapper;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.exception.SurveyErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.token.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyQuery surveyQuery;
    private final AuthQuery authQuery;
    private final CookieUtil cookieUtil;

    @Transactional
    public void createSurvey(SurveyCreateRequest request, Member member,
            HttpServletResponse response) {
        if (surveyQuery.existsByMember(member)) {
            throw new RestApiException(SurveyErrorCode.SURVEY_ALREADY_EXISTS_FOR_MEMBER);
        }

        List<String> contentMetaDataIds = new ArrayList<>();
        if (request.contentIds() != null && !request.contentIds().isEmpty()) {
            for (Long contentId : request.contentIds()) {
                contentMetaDataIds.add(
                        String.valueOf(surveyQuery.findContentMetadataId(contentId)));
            }
        }
        Survey survey = SurveyMapper.toEntity(request, member, contentMetaDataIds);
        surveyQuery.save(survey);

        member.updateRole(ROLE_USER);
        authQuery.save(member);

        cookieUtil.deleteCookie(response);

        response.setHeader("Access-Control-Expose-Headers", "X-New-User");
        response.setHeader("X-New-User", "true");
    }
}
