package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.member.dto.response.MemberCuratedContentGetResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface CuratedContentQueryDSL {

    CursorPageResponse<MemberCuratedContentGetResponse> getCuratedContentByCursor(
            Long cursor, int size, Member member);
}
