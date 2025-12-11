package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.service.ContentRecommendationQuery;
import com.example.udtbe.domain.content.service.RecommendationResponseBuilder;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RecommendationResponseBuilderTest {

    @Mock
    private ContentRecommendationQuery contentRecommendationQuery;

    @InjectMocks
    private RecommendationResponseBuilder responseBuilder;

    @Test
    @DisplayName("추천 응답 빌드 - 정상적인 경우")
    void buildResponseFromRecommendations_Success() {
        // given
        Content content1 = createContentWithId(1L, "기생충");
        Content content2 = createContentWithId(2L, "올드보이");
        Content content3 = createContentWithId(3L, "인터스텔라");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스",
                "스릴러,드라마");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스",
                "스릴러,액션");
        ContentMetadata metadata3 = ContentMetadataFixture.metadata(content3, "넷플릭스", "SF");

        List<ContentRecommendationDTO> recommendations = List.of(
                new ContentRecommendationDTO(1L, 5.0f),
                new ContentRecommendationDTO(2L, 4.5f),
                new ContentRecommendationDTO(3L, 4.0f)
        );

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2,
                3L, metadata3
        );

        when(contentRecommendationQuery.findContentsByIds(anyList()))
                .thenReturn(List.of(content1, content2, content3));

        // when
        List<ContentRecommendationResponse> result = responseBuilder.buildResponseFromRecommendations(
                recommendations, metadataCache);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).title()).isEqualTo("기생충");
        assertThat(result.get(1).title()).isEqualTo("올드보이");
        assertThat(result.get(2).title()).isEqualTo("인터스텔라");
    }

    @Test
    @DisplayName("추천 응답 빌드 - 빈 추천 리스트")
    void buildResponseFromRecommendations_WithEmptyRecommendations() {
        // given
        List<ContentRecommendationDTO> emptyRecommendations = List.of();
        Map<Long, ContentMetadata> metadataCache = Map.of();

        when(contentRecommendationQuery.findContentsByIds(anyList()))
                .thenReturn(List.of());

        // when
        List<ContentRecommendationResponse> result = responseBuilder.buildResponseFromRecommendations(
                emptyRecommendations, metadataCache);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("추천 응답 빌드 - 메타데이터가 없는 콘텐츠는 기본 응답 반환")
    void buildResponseFromRecommendations_ReturnsDefaultResponseForContentWithoutMetadata() {
        // given
        Content content1 = createContentWithId(1L, "기생충");
        Content content2 = createContentWithId(2L, "올드보이"); // 이 콘텐츠는 메타데이터 없음

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "스릴러");

        List<ContentRecommendationDTO> recommendations = List.of(
                new ContentRecommendationDTO(1L, 5.0f),
                new ContentRecommendationDTO(2L, 4.5f)  // content2는 메타데이터 없음
        );

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1
                // 2L은 없음 - 메타데이터가 없는 경우
        );

        when(contentRecommendationQuery.findContentsByIds(anyList()))
                .thenReturn(List.of(content1, content2));

        // when
        List<ContentRecommendationResponse> result = responseBuilder.buildResponseFromRecommendations(
                recommendations, metadataCache);

        // then
        assertThat(result).hasSize(2); // content2는 기본 응답으로 반환됨
        assertThat(result.get(0).title()).isEqualTo("기생충");
        assertThat(result.get(0).genres()).isNotEmpty(); // metadata 있음

        assertThat(result.get(1).title()).isEqualTo("올드보이");
        assertThat(result.get(1).genres()).isEmpty(); // metadata 없으면 빈 리스트
    }

    @Test
    @DisplayName("추천 응답 빌드 - 추천 순서대로 Content를 조회")
    void buildResponseFromRecommendations_PreservesOrder() {
        // given
        Content content1 = createContentWithId(1L, "첫번째");
        Content content2 = createContentWithId(2L, "두번째");
        Content content3 = createContentWithId(3L, "세번째");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "코미디");
        ContentMetadata metadata3 = ContentMetadataFixture.metadata(content3, "넷플릭스", "드라마");

        // 점수 순서: 3L > 1L > 2L
        List<ContentRecommendationDTO> recommendations = List.of(
                new ContentRecommendationDTO(3L, 10.0f),
                new ContentRecommendationDTO(1L, 8.0f),
                new ContentRecommendationDTO(2L, 6.0f)
        );

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2,
                3L, metadata3
        );

        // findContentsByIds는 요청된 순서대로 반환한다고 가정
        when(contentRecommendationQuery.findContentsByIds(List.of(3L, 1L, 2L)))
                .thenReturn(List.of(content3, content1, content2));

        // when
        List<ContentRecommendationResponse> result = responseBuilder.buildResponseFromRecommendations(
                recommendations, metadataCache);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).title()).isEqualTo("세번째");
        assertThat(result.get(1).title()).isEqualTo("첫번째");
        assertThat(result.get(2).title()).isEqualTo("두번째");
    }

    @Test
    @DisplayName("추천 응답 빌드 - Content ID 추출 확인")
    void buildResponseFromRecommendations_ExtractsContentIds() {
        // given
        Content content1 = createContentWithId(1L, "영화1");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");

        List<ContentRecommendationDTO> recommendations = List.of(
                new ContentRecommendationDTO(1L, 5.0f)
        );

        Map<Long, ContentMetadata> metadataCache = Map.of(1L, metadata1);

        when(contentRecommendationQuery.findContentsByIds(List.of(1L)))
                .thenReturn(List.of(content1));

        // when
        List<ContentRecommendationResponse> result = responseBuilder.buildResponseFromRecommendations(
                recommendations, metadataCache);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).contentId()).isEqualTo(1L);
    }

    // === Helper 메서드들 ===

    private Content createContentWithId(Long id, String title) {
        Content content = ContentFixture.content(title, "설명");
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }
}