package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;

public interface CuratedContentQueryDSL {

    List<CuratedContent> getCuratedContentByCursor(
            CuratedContentGetRequest curatedContentGetRequest, Member member);
}
