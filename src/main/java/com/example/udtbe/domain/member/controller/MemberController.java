package com.example.udtbe.domain.member.controller;

import com.example.udtbe.domain.content.dto.request.CuratedContentListDeleteRequest;
import com.example.udtbe.domain.content.service.ContentRecommendationService;
import com.example.udtbe.domain.content.service.ContentService;
import com.example.udtbe.domain.member.dto.request.MemberCuratedContentGetsRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdatePlatformRequest;
import com.example.udtbe.domain.member.dto.response.MemberCuratedContentGetResponse;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdateGenreResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdatePlatformResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.service.MemberService;
import com.example.udtbe.global.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerApiSpec {

    private final MemberService memberService;
    private final ContentService contentService;
    private final ContentRecommendationService contentRecommendationService;

    @Override
    public ResponseEntity<MemberInfoResponse> getMemberInfo(Member member) {
        MemberInfoResponse response = memberService.getMemberInfo(member.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CursorPageResponse<MemberCuratedContentGetResponse>> getCuratedContents(
            Member member,
            MemberCuratedContentGetsRequest memberCuratedContentGetsRequest) {
        CursorPageResponse<MemberCuratedContentGetResponse> response
                = memberService.getCuratedContents(memberCuratedContentGetsRequest, member);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<MemberUpdateGenreResponse> updateSurveyGenres(Member member,
            MemberUpdateGenreRequest memberUpdateGenreRequest) {

        MemberUpdateGenreResponse memberUpdateGenreResponse = memberService.updateMemberGenres(
                member.getId(), memberUpdateGenreRequest);
        return ResponseEntity.ok(memberUpdateGenreResponse);
    }

    @Override
    public ResponseEntity<MemberUpdatePlatformResponse> updateSurveyPlatforms(Member member,
            MemberUpdatePlatformRequest memberUpdateGenreRequest) {
        MemberUpdatePlatformResponse memberUpdatePlatformResponse = memberService.updateMemberPlatforms(
                member.getId(), memberUpdateGenreRequest
        );
        return ResponseEntity.ok(memberUpdatePlatformResponse);

    }

    @Override
    public ResponseEntity<Void> deleteCuratedContents(CuratedContentListDeleteRequest request,
            Member member) {
        contentService.deleteCuratedContents(member.getId(), request.contentIds());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> clearMyRecommendationCache(Member member) {
        contentRecommendationService.clearMyRecommendationCache(member);
        return ResponseEntity.ok().build();
    }

}
