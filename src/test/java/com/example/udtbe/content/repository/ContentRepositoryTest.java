package com.example.udtbe.content.repository;

import static com.example.udtbe.domain.content.entity.enums.CategoryType.DRAMA;
import static com.example.udtbe.domain.content.entity.enums.CategoryType.MOVIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.udtbe.common.fixture.CategoryFixture;
import com.example.udtbe.common.fixture.ContentCategoryFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.admin.dto.response.AdminContentCategoryMetricResponse;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.CategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ContentRepositoryTest extends DataJpaSupport {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ContentCategoryRepository contentCategoryRepository;

    @DisplayName("삭제된 콘텐츠를 조회할 수 없다.")
    @Test
    void throwExceptionWhenContentIsNotExist() {
        // when  // then
        assertThatThrownBy(() -> contentRepository.getContentDetails(1L))
                .isInstanceOf(RestApiException.class)
                .hasMessage(ContentErrorCode.CONTENT_NOT_FOUND.getMessage());
    }

    @DisplayName("콘텐츠 카테고리 별 총 개수 지표를 가져온다.")
    @Test
    void getContentCategoryMetric() {
        // given
        Category movie = CategoryFixture.category(MOVIE);
        Category drama = CategoryFixture.category(DRAMA);
        List<Category> savedCategories = categoryRepository.saveAll(List.of(movie, drama));

        Content content1 = ContentFixture.content("영화1", "영화1");
        Content content2 = ContentFixture.content("영화2", "영화2");
        Content content3 = ContentFixture.content("드라마", "드라마");
        contentRepository.saveAll(List.of(content1, content2, content3));

        ContentCategory contentCategory1 = ContentCategoryFixture.contentCategory(content1,
                savedCategories.get(0));
        ContentCategory contentCategory2 = ContentCategoryFixture.contentCategory(content2,
                savedCategories.get(0));
        ContentCategory contentCategory3 = ContentCategoryFixture.contentCategory(content3,
                savedCategories.get(1));

        contentCategoryRepository.saveAll(
                List.of(contentCategory1, contentCategory2, contentCategory3)
        );

        // when
        AdminContentCategoryMetricResponse response = contentRepository.getContentCategoryMetric();
        // then
        assertAll(
                () -> assertThat(response.categoryMetrics().get(0).categoryType())
                        .isEqualTo(movie.getCategoryType().getType()),
                () -> assertThat(response.categoryMetrics().get(1).categoryType())
                        .isEqualTo(drama.getCategoryType().getType())
        );
    }
}
