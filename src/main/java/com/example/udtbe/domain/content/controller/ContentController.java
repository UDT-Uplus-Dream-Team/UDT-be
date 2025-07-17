package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.service.ContentService;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentController implements ContentControllerApiSpec {

    private final ContentService contentService;

    @Override
    public ResponseEntity<CursorPageResponse<ContentsGetResponse>> getContents(
            ContentsGetRequest request) {
        CursorPageResponse<ContentsGetResponse> response = contentService.getContents(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ContentDetailsGetResponse> getContentDetails(Long contentId) {
        ContentDetailsGetResponse response = contentService.getContentDetails(contentId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<WeeklyRecommendedContentsResponse>> getWeeklyRecommendedContents(
            WeeklyRecommendationRequest request) {
        List<WeeklyRecommendedContentsResponse> response =
                contentService.getWeeklyRecommendedContents(request);
        return ResponseEntity.ok(response);
    }
}
