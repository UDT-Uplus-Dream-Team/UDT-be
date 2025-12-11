package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class RecommendationQueryBuilder {

    public List<Long> getPlatformFilteredContentIds(
            List<String> platformTags,
            Map<Long, ContentMetadata> metadataCache) {

        if (platformTags == null || platformTags.isEmpty()) {
            return new ArrayList<>(metadataCache.keySet());
        }

        List<String> koreanPlatformTags = PlatformType.toKoreanTypes(platformTags);

        if (koreanPlatformTags.isEmpty()) {
            log.warn("플랫폼 태그 변환 실패 - 모든 콘텐츠 반환");
            return new ArrayList<>(metadataCache.keySet());
        }

        Set<Long> contentIds = new HashSet<>();
        for (String koreanPlatformTag : koreanPlatformTags) {
            if (StringUtils.hasText(koreanPlatformTag)) {
                for (Map.Entry<Long, ContentMetadata> entry : metadataCache.entrySet()) {
                    ContentMetadata metadata = entry.getValue();
                    if (metadata.getPlatformTag() != null &&
                            metadata.getPlatformTag().contains(koreanPlatformTag)) {
                        contentIds.add(entry.getKey());
                    }
                }
            }
        }

        return new ArrayList<>(contentIds);
    }
}