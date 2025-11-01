package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.service.RecommendationQueryBuilder;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RecommendationQueryBuilderTest {

    private final RecommendationQueryBuilder queryBuilder = new RecommendationQueryBuilder();

    @Test
    @DisplayName("플랫폼 필터링 - 넷플릭스 플랫폼만 필터링")
    void getPlatformFilteredContentIds_WithNetflixOnly() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");
        Content content3 = createContentWithId(3L, "영화3");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "왓챠", "코미디");
        ContentMetadata metadata3 = ContentMetadataFixture.metadata(content3, "넷플릭스,왓챠", "드라마");

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2,
                3L, metadata3
        );

        // when - NETFLIX는 영어이므로 toKoreanTypes로 변환됨
        List<Long> result = queryBuilder.getPlatformFilteredContentIds(
                List.of("NETFLIX"), metadataCache);

        // then
        assertThat(result).containsExactlyInAnyOrder(1L, 3L); // 넷플릭스 포함된 콘텐츠만
    }

    @Test
    @DisplayName("플랫폼 필터링 - 여러 플랫폼 OR 조건")
    void getPlatformFilteredContentIds_WithMultiplePlatforms() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");
        Content content3 = createContentWithId(3L, "영화3");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "왓챠", "코미디");
        ContentMetadata metadata3 = ContentMetadataFixture.metadata(content3, "티빙", "드라마");

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2,
                3L, metadata3
        );

        // when
        List<Long> result = queryBuilder.getPlatformFilteredContentIds(
                List.of("NETFLIX", "WATCHA"), metadataCache);

        // then
        assertThat(result).containsExactlyInAnyOrder(1L, 2L); // 넷플릭스 or 왓챠
    }

    @Test
    @DisplayName("플랫폼 필터링 - 빈 플랫폼 리스트면 모든 콘텐츠 반환")
    void getPlatformFilteredContentIds_WithEmptyPlatforms() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "왓챠", "코미디");

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2
        );

        // when
        List<Long> result1 = queryBuilder.getPlatformFilteredContentIds(
                List.of(), metadataCache);
        List<Long> result2 = queryBuilder.getPlatformFilteredContentIds(
                null, metadataCache);

        // then
        assertThat(result1).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result2).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("플랫폼 필터링 - 일치하는 플랫폼이 없으면 빈 리스트")
    void getPlatformFilteredContentIds_WithNoMatchingPlatform() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "왓챠", "코미디");

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2
        );

        // when
        List<Long> result = queryBuilder.getPlatformFilteredContentIds(
                List.of("TVING"), metadataCache);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("플랫폼 필터링 - 콘텐츠가 여러 플랫폼을 가진 경우")
    void getPlatformFilteredContentIds_WithMultiplePlatformsPerContent() {
        // given
        Content content1 = createContentWithId(1L, "영화1");

        // 넷플릭스, 왓챠, 티빙 모두 포함
        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스,왓챠,티빙",
                "액션");

        Map<Long, ContentMetadata> metadataCache = Map.of(1L, metadata1);

        // when
        List<Long> netflixResult = queryBuilder.getPlatformFilteredContentIds(
                List.of("NETFLIX"), metadataCache);
        List<Long> watchaResult = queryBuilder.getPlatformFilteredContentIds(
                List.of("WATCHA"), metadataCache);
        List<Long> tvingResult = queryBuilder.getPlatformFilteredContentIds(
                List.of("TVING"), metadataCache);

        // then
        assertThat(netflixResult).containsExactly(1L);
        assertThat(watchaResult).containsExactly(1L);
        assertThat(tvingResult).containsExactly(1L);
    }

    @Test
    @DisplayName("플랫폼 필터링 - 중복 제거")
    void getPlatformFilteredContentIds_RemovesDuplicates() {
        // given
        Content content1 = createContentWithId(1L, "영화1");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스,왓챠",
                "액션");

        Map<Long, ContentMetadata> metadataCache = Map.of(1L, metadata1);

        // when - 넷플릭스와 왓챠 둘 다 요청하지만 content1은 둘 다 포함
        List<Long> result = queryBuilder.getPlatformFilteredContentIds(
                List.of("NETFLIX", "WATCHA"), metadataCache);

        // then - 중복 없이 1L만 한 번
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(1L);
    }

    // === Helper 메서드들 ===

    private Content createContentWithId(Long id, String title) {
        Content content = ContentFixture.content(title, "설명");
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }
}