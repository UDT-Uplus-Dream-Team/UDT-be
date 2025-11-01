package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.ContentRecommendationMapper;
import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationResponseBuilder {

    private final ContentRecommendationQuery contentRecommendationQuery;

    public List<ContentRecommendationResponse> buildResponseFromRecommendations(
            List<ContentRecommendationDTO> recommendations,
            Map<Long, ContentMetadata> metadataCache) {

        List<Long> recommendedContentIds = recommendations.stream()
                .map(ContentRecommendationDTO::contentId)
                .toList();

        List<Content> contents = contentRecommendationQuery.findContentsByIds(
                recommendedContentIds);
        List<ContentMetadata> metadataList = contents.stream()
                .map(content -> metadataCache.get(content.getId()))
                .filter(Objects::nonNull)
                .toList();

        for (Content content : contents) {
            log.info("추출된 순서 : {}", content.getTitle());
        }

        return ContentRecommendationMapper.toResponseList(contents, metadataList);
    }
}