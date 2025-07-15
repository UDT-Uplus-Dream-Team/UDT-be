package com.example.udtbe.domain.member.controller;

import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.dto.response.CuratedContentGetListResponse;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerApiSpec {

    private final MemberService memberService;

    @Override
    public ResponseEntity<MemberInfoResponse> getMemberInfo(Member member) {
        MemberInfoResponse response = memberService.getMemberInfo(member.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CuratedContentGetListResponse> getCuratedContents(Member member,
            CuratedContentGetRequest curatedContentGetRequest) {
        // TODO: 3차 MVP 때 엄선된 콘텐츠 필터링 반영
        CuratedContentGetListResponse response
                = memberService.getCuratedContents(curatedContentGetRequest, member);
        return ResponseEntity.ok(response);
    }

}
