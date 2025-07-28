package com.example.udtbe.domain.content.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ContentRecommendationMapper {

    public static ContentRecommendationResponse toResponse(Content content,
            ContentMetadata metadata) {
        return new ContentRecommendationResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getOpenDate() != null ? content.getOpenDate().toString() : null,
                content.getRunningTime(),
                content.getEpisode() != 0 ? content.getEpisode() + "회차" : "0",
                content.getRating(),
                metadata.getCategoryTag() != null && !metadata.getCategoryTag().isEmpty()
                        ? metadata.getCategoryTag().get(0) : null,
                metadata.getGenreTag() != null ? metadata.getGenreTag() : Collections.emptyList(),
                metadata.getDirectorTag() != null ? metadata.getDirectorTag()
                        : Collections.emptyList(),
                metadata.getCastTag() != null ? metadata.getCastTag() : Collections.emptyList(),
                metadata.getPlatformTag() != null ? metadata.getPlatformTag()
                        : Collections.emptyList(),
                content.getContentPlatforms() != null ? content.getContentPlatforms().stream()
                        .map(ContentPlatform::getWatchUrl)
                        .filter(Objects::nonNull)
                        .toList() : Collections.emptyList()
        );
    }

    public static List<ContentRecommendationResponse> toResponseList(
            List<Content> contents,
            List<ContentMetadata> metadataList) {

        Map<Long, ContentMetadata> metadataMap = metadataList.stream()
                .collect(Collectors.toMap(
                        metadata -> metadata.getContent().getId(),
                        metadata -> metadata
                ));

        return contents.stream()
                .map(content -> {
                    ContentMetadata metadata = metadataMap.get(content.getId());
                    if (metadata != null) {
                        return toResponse(content, metadata);
                    }
                    return toDefaultResponse(content);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static ContentRecommendationResponse toDefaultResponse(Content content) {
        return new ContentRecommendationResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getOpenDate() != null ? content.getOpenDate().toLocalDate().toString()
                        : null,
                content.getRunningTime(),
                content.getEpisode() != 0 ? content.getEpisode() + "회차" : "0",
                content.getRating(),
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                content.getContentPlatforms() != null ? content.getContentPlatforms().stream()
                        .map(contentPlatform -> contentPlatform.getWatchUrl())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }
}