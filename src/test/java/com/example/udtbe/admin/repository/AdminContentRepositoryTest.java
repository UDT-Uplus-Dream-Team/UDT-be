package com.example.udtbe.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.udtbe.common.fixture.ContentCastFixture;
import com.example.udtbe.common.fixture.ContentCategoryFixture;
import com.example.udtbe.common.fixture.ContentCountryFixture;
import com.example.udtbe.common.fixture.ContentDirectorFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentGenreFixture;
import com.example.udtbe.common.fixture.ContentPlatformFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:data-test.sql")
public class AdminContentRepositoryTest extends DataJpaSupport {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentMetadataRepository metadataRepository;


    @DisplayName("관리자는 콘텐츠를 저장할 수 있다.")
    @Test
    @Rollback
    void saveContent() {
        // given
        Content content = ContentFixture.content("해리포터", "빅잼");
        // when
        Content save = contentRepository.save(content);
        List<ContentCategory> contentCategories = ContentCategoryFixture.contentCategories(content);
        List<ContentPlatform> contentPlatforms = ContentPlatformFixture.contentPlatforms(content,
                3);
        List<ContentCountry> contentCountries = ContentCountryFixture.contentCountries(content, 3);
        List<ContentGenre> contentGenres = ContentGenreFixture.contentGenres(contentCategories,
                content);
        List<ContentCast> contentCasts = ContentCastFixture.contentCasts(content, 30);
        List<ContentDirector> contentDirectors = ContentDirectorFixture.contentDirectors(content,
                3);

        // then
        assertAll("연관관계까지 함께 저장 및 로딩",
                () -> assertThat(save.getId()).isNotNull(),
                () -> assertThat(save.getTitle()).isEqualTo(content.getTitle()),
                () -> assertThat(save.getContentPlatforms().size()).isEqualTo(
                        contentPlatforms.size()),
                () -> assertThat(save.getContentDirectors().size()).isEqualTo(
                        contentDirectors.size()),
                () -> assertThat(save.getContentCategories().size()).isEqualTo(
                        contentCategories.size()),
                () -> assertThat(save.getContentCasts().size()).isEqualTo(contentCasts.size()),
                () -> assertThat(save.getContentCountries().size()).isEqualTo(
                        contentCountries.size()),
                () -> assertThat(save.getContentGenres().size()).isEqualTo(contentGenres.size())
        );
    }

    @DisplayName("관리자는 콘텐츠메타데이터를 저장할 수 있다.")
    @Test
    @Rollback
    public void saveContentMeta() {
        // given
        Content content = ContentFixture.content("인터스텔라", "꿀잼");
        contentRepository.save(content);

        ContentMetadata contentMetadata = ContentFixture.contentMetadata("인터스텔라", content);
        // when
        ContentMetadata save = metadataRepository.save(contentMetadata);

        assertAll("ContentMetadata 저장 및 조회",
                () -> assertThat(save.getId()).isNotNull(),
                () -> assertThat(save.getContent().getId()).isEqualTo(
                        contentMetadata.getContent().getId()),
                () -> assertThat(save.getTitle()).isEqualTo(contentMetadata.getTitle()),
                () -> assertThat(save.getRating()).isEqualTo(contentMetadata.getRating()),
                () -> assertThat(save.isDeleted()).isFalse(),
                () -> assertThat(save.getGenreTag()).hasSize(3),
                () -> {
                    assertNotNull(save.getGenreTag());
                    assertThat(save.getGenreTag().get(1)).isEqualTo(
                            contentMetadata.getGenreTag().get(1));
                },
                () -> assertThat(save.getPlatformTag()).hasSize(3),
                () -> assertThat(save.getDirectorTag()).hasSize(2)
        );
    }

    @DisplayName("첫 페이지를 조회 할 수 있다.( cursor=null, size=5 )")
    @Test
    @Rollback
    void findContentsAdminByCursor() {
        // given
        List<Content> contents = ContentFixture.contents(10);
        contents.forEach(em::persist);
        em.flush();
        em.clear();
        int size = 5;
        Long lastId = contents.get(contents.size() - 1).getId();

        // when
        CursorPageResponse<AdminContentGetResponse> page =
                contentRepository.getsAdminContents(null, size, null);

        // then
        List<AdminContentGetResponse> dtos = page.item();
        assertThat(dtos).hasSize(size)
                .extracting(AdminContentGetResponse::contentId)
                .containsExactly(
                        IntStream.range(0, size).mapToObj(i -> lastId - i).toArray(Long[]::new));
        assertThat(page.hasNext()).isTrue();
        assertThat(page.nextCursor()).isEqualTo(String.valueOf(lastId - size + 1));
    }
}
