package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.exception.RecommendContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentRecommendationQuery {

    private final ContentMetadataRepository contentMetadataRepository;
    private final SurveyRepository surveyRepository;
    private final FeedbackRepository feedbackRepository;
    private final ContentRepository contentRepository;

    /**
     * 회원별 설문 조회
     */
    public Survey findSurveyByMemberId(Long memberId) {
        return surveyRepository.findByMemberId(memberId).orElseThrow(
                () -> new RestApiException(RecommendContentErrorCode.SURVEY_NOT_FOUND));
    }

    /**
     * 모든 ContentMetadata 캐시 생성
     */
    public Map<Long, ContentMetadata> findContentMetadataCache() {
        try {
            return contentMetadataRepository.findByIsDeletedFalse()
                    .stream()
                    .filter(metadata -> metadata.getContent() != null)
                    .collect(Collectors.toMap(
                            metadata -> metadata.getContent().getId(),
                            metadata -> metadata
                    ));
        } catch (OutOfMemoryError e) {
            log.error("!!!메모리 부족으로 ContentMetadata 캐시 생성 실패 - OOM!!! ", e);
            throw new RestApiException(RecommendContentErrorCode.CONTENT_METADATA_CACHE_ERROR);
        } catch (Exception e) {
            log.warn("ContentMetadata 캐시 생성 실패: {}", e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.CONTENT_METADATA_CACHE_ERROR);
        }
    }

    /**
     * 회원별 피드백 조회
     */
    public List<Feedback> findFeedbacksByMemberId(Long memberId) {
        try {
            return feedbackRepository.findByMemberIdAndIsDeletedFalse(memberId);
        } catch (Exception e) {
            log.warn("피드백 조회 실패: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.FEEDBACK_RETRIEVAL_ERROR);
        }
    }

    /**
     * ID 목록으로 컨텐츠 조회
     */
    public List<Content> findContentsByIds(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return contentRepository.findAllById(contentIds);
        } catch (Exception e) {
            log.warn("컨텐츠 조회 실패: error={}", e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.CONTENT_BATCH_RETRIEVAL_ERROR);
        }
    }
}
