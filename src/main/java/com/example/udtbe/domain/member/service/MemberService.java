package com.example.udtbe.domain.member.service;

import com.example.udtbe.domain.content.dto.CuratedContentMapper;
import com.example.udtbe.domain.content.dto.common.CuratedContentDTO;
import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.dto.response.CuratedContentGetListResponse;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.content.service.CuratedContentQuery;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdatePlatformRequest;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdateGenreResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdatePlatformResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.service.SurveyQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberQuery memberQuery;
    private final SurveyQuery surveyQuery;
    private final CuratedContentQuery curatedContentQuery;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(Long memberId) {

        Member member = memberQuery.findMemberById(memberId);
        Survey survey = surveyQuery.findSurveyByMemberId(memberId);

        return new MemberInfoResponse(
                member.getName(),
                member.getEmail(),
                survey.getPlatformTag(),
                survey.getGenreTag(),
                member.getProfileImageUrl());
    }

    @Transactional(readOnly = true)
    public CuratedContentGetListResponse getCuratedContents(CuratedContentGetRequest request,
            Member member) {

        List<CuratedContent> curatedContents = curatedContentQuery.getCuratedContentsByCursor(
                member, request);

        boolean hasNext = curatedContents.size() > request.size();
        List<CuratedContent> limited =
                hasNext ? curatedContents.subList(0, request.size()) : curatedContents;

        List<CuratedContentDTO> dtoList = CuratedContentMapper.toResponseList(limited);

        Long nextCursor = hasNext ? limited.get(limited.size() - 1).getId() : null;

        return new CuratedContentGetListResponse(
                dtoList, nextCursor, hasNext
        );
    }

    @Transactional
    public MemberUpdateGenreResponse updateMemberGenres(Long memberId,
            MemberUpdateGenreRequest memberUpdateGenreRequest) {
        Survey survey = surveyQuery.findSurveyByMemberId(memberId);

        List<String> genres = memberUpdateGenreRequest.genres().stream().map(genreType ->
                GenreType.fromByType(genreType).name()
        ).toList();

        survey.updateGenreTag(genres);

        List<String> genresRes = survey.getGenreTag().stream()
                .map(genreType ->
                        GenreType.from(genreType).getType()
                ).toList();

        return new MemberUpdateGenreResponse(genresRes);
    }

    @Transactional
    public MemberUpdatePlatformResponse updateMemberPlatforms(Long memberId,
            MemberUpdatePlatformRequest memberUpdatePlatformRequest) {
        Survey survey = surveyQuery.findSurveyByMemberId(memberId);

        List<String> platforms = memberUpdatePlatformRequest.platforms().stream()
                .map(platformType ->
                        PlatformType.fromByType(platformType).name()
                ).toList();

        survey.updatePlatformTag(platforms);

        List<String> platformsRes = survey.getPlatformTag().stream()
                .map(platformType ->
                        PlatformType.from(platformType).getType()
                ).toList();

        return new MemberUpdatePlatformResponse(platformsRes);
    }

}
