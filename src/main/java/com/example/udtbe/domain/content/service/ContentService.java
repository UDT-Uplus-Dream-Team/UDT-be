package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentQuery contentQuery;

    @Transactional(readOnly = true)
    public CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request) {
        return contentQuery.getContents(request);
    }

    @Transactional(readOnly = true)
    public ContentDetailsGetResponse getContentDetails(Long contentId) {
        return contentQuery.getContentDetails(contentId);
    }
}
