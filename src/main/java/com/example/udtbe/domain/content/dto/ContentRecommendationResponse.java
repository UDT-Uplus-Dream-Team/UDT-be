package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record ContentRecommendationResponse(
        Long contentId,
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        String openDate,
        int runningTime,
        String episode,
        String rating,
        String category,
        List<String> genres,
        List<String> directors,
        List<String> platforms
) {

    public static ContentRecommendationResponse of(Content content, ContentMetadata metadata) {
        return new ContentRecommendationResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getOpenDate() != null ? content.getOpenDate().toLocalDate().toString()
                        : null,
                content.getRunningTime(),
                content.getEpisode() != 0 ? content.getEpisode() + "회차" : "에피소드 없음",
                content.getRating(),
//                metadata.getCategoryTag() != null && !metadata.getCategoryTag().isEmpty()
//                        ? metadata.getCategoryTag().get(0) : null,
                convertCategoryToKorean(metadata.getCategoryTag()),
//                metadata.getGenreTag() != null ? metadata.getGenreTag() : List.of(),
                convertGenresToKorean(metadata.getGenreTag()),
                metadata.getDirectorTag() != null ? metadata.getDirectorTag() : List.of(),
//                metadata.getPlatformTag() != null ? metadata.getPlatformTag() : List.of()
                convertPlatformsToKorean(metadata.getPlatformTag())
        );
    }


    public static List<ContentRecommendationResponse> of(
            List<Content> contents,
            List<ContentMetadata> metadataList) {

        // ContentMetadata를 contentId 기준으로 맵으로 변환
        Map<Long, ContentMetadata> metadataMap = metadataList.stream()
                .collect(Collectors.toMap(
                        metadata -> metadata.getContent().getId(),
                        metadata -> metadata
                ));

        // Content와 해당하는 ContentMetadata를 매핑하여 Response 생성
        return contents.stream()
                .map(content -> {
                    ContentMetadata metadata = metadataMap.get(content.getId());
                    if (metadata != null) {
                        return of(content, metadata);
                    }
                    // metadata가 없는 경우 기본값으로 처리 (또는 예외 처리)
                    return createDefaultResponse(content);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static ContentRecommendationResponse createDefaultResponse(Content content) {
        return new ContentRecommendationResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getOpenDate() != null ? content.getOpenDate().toLocalDate().toString()
                        : null,
                content.getRunningTime(),
                content.getEpisode() != 0 ? content.getEpisode() + "회차" : "에피소드 없음",
                content.getRating(),
                null, // category
                List.of(), // genres
                List.of(), // directors
                List.of()  // platforms
        );

    }

    private static String convertCategoryToKorean(List<String> categoryTags) {
        if (categoryTags == null || categoryTags.isEmpty()) {
            return null;
        }
        try {
            return CategoryType.valueOf(categoryTags.get(0)).getType();
        } catch (IllegalArgumentException e) {
            return categoryTags.get(0);
        }
    }

    private static List<String> convertGenresToKorean(List<String> genreTags) {
        if (genreTags == null) {
            return List.of();
        }
        return genreTags.stream()
                .map(genre -> {
                    try {
                        return GenreType.valueOf(genre).getType();
                    } catch (IllegalArgumentException e) {
                        return genre;
                    }
                })
                .toList();
    }

    private static List<String> convertPlatformsToKorean(List<String> platformTags) {
        if (platformTags == null) {
            return List.of();
        }
        return platformTags.stream()
                .map(platform -> {
                    try {
                        return PlatformType.valueOf(platform).getType();
                    } catch (IllegalArgumentException e) {
                        return platform;
                    }
                })
                .toList();
    }
}