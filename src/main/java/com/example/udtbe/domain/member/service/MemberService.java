package com.example.udtbe.domain.member.service;

import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.service.SurveyQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberQuery memberQuery;
    private final SurveyQuery surveyQuery;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(Long memberId) {

        Member member = memberQuery.findMemberById(memberId);
        Survey survey = surveyQuery.findSurveyByMemberId(memberId);

        return new MemberInfoResponse(
                member.getName(),
                member.getEmail(),
                survey.getGenreTag(),
                survey.getPlatformTag(),
                member.getProfileImageUrl());

    }
}
