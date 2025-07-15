package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.CuratedContentRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CuratedContentQuery {

    private final ContentRepository contentRepository;
    private final CuratedContentRepository curatedContentRepository;

    public CuratedContent findCuratedContent(Long curatedContentId) {
        return curatedContentRepository.findCuratedContentById(curatedContentId)
                .orElseThrow(
                        () -> new RestApiException(ContentErrorCode.CURATED_CONTENT_NOT_FOUND));
    }

    public List<CuratedContent> getCuratedContentsByCursor(Member member,
            CuratedContentGetRequest curatedContentGetRequest) {
        return curatedContentRepository.getCuratedContentByCursor(curatedContentGetRequest, member);
    }
}
