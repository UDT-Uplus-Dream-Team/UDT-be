package com.example.udtbe.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.ArrayList;
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


    @DisplayName("관리자는 콘텐츠를 저장한다.")
    @Test
    @Rollback
    void saveContent() {
        // given
        Content content = ContentFixture.content("해리포터", "빅잼");
        // when
        Content save = contentRepository.save(content);
        List<ContentCategory> contentCategories = defaultCategories(content);
        List<ContentPlatform> contentPlatforms = defaultPlatforms(content, 3);
        List<ContentCountry> contentCountries = defaultCountries(content, 3);
        List<ContentGenre> contentGenres = defaultGenres(contentCategories, content);
        List<ContentCast> contentCasts = defaultCasts(content, 30);
        List<ContentDirector> contentDirectors = defaultDirectors(content, 3);

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

    @DisplayName("관리자는 콘텐츠메타데이터를 저장한다.")
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

    @DisplayName("첫 페이지 조회: cursor=null, size=5")
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
        CursorPageResponse<ContentDTO> page =
                contentRepository.findContentsAdminByCursor(null, size);

        // then
        List<ContentDTO> dtos = page.item();
        assertThat(dtos).hasSize(size)
                .extracting(ContentDTO::contentId)
                .containsExactly(
                        IntStream.range(0, size).mapToObj(i -> lastId - i).toArray(Long[]::new));
        assertThat(page.hasNext()).isTrue();
        assertThat(page.nextCursor()).isEqualTo(String.valueOf(lastId - size + 1));
    }


    private List<ContentPlatform> defaultPlatforms(Content content, int count) {
        List<ContentPlatform> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Platform platform = Platform.of(PlatformType.NETFLIX);
            ContentPlatform contentPlatform = ContentPlatform.of("https://example.com/watch" + i,
                    true, content,
                    platform);
            list.add(contentPlatform);
        });
        return list;
    }

    private List<ContentCast> defaultCasts(Content content, int count) {
        List<ContentCast> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Cast cast = Cast.of("박연진" + i, "https://example.com/cast" + i);
            ContentCast contentCast = ContentCast.of(content, cast);
            list.add(contentCast);
        });
        return list;
    }

    private List<ContentDirector> defaultDirectors(Content content, int count) {
        List<ContentDirector> list = new ArrayList<>();
        IntStream.rangeClosed(1, count).forEach(i -> {
            Director director = Director.of("감스트" + i);
            ContentDirector contentDirector = ContentDirector.of(content, director);
            list.add(contentDirector);
        });
        return list;
    }

    private List<ContentCountry> defaultCountries(Content content, int count) {
        List<ContentCountry> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Country country = Country.of("대한민국" + i);
            ContentCountry contentCountry = ContentCountry.of(content, country);
            list.add(contentCountry);
        });
        return list;
    }

    private List<ContentCategory> defaultCategories(Content content) {
        List<ContentCategory> list = new ArrayList<>();
        Category category1 = Category.of(CategoryType.DRAMA);
        Category category2 = Category.of(CategoryType.MOVIE);
        Category category3 = Category.of(CategoryType.ANIMATION);
        ContentCategory contentCategory1 = ContentCategory.of(content, category1);
        ContentCategory contentCategory2 = ContentCategory.of(content, category2);
        ContentCategory contentCategory3 = ContentCategory.of(content, category3);

        list.add(contentCategory1);
        list.add(contentCategory2);
        list.add(contentCategory3);
        return list;
    }

    private List<ContentGenre> defaultGenres(List<ContentCategory> contentCategories,
            Content content) {
        List<ContentGenre> list = new ArrayList<>();

        contentCategories.forEach(c -> {
            if (c.getCategory().getCategoryType().equals(CategoryType.DRAMA)) {
                Genre genre1 = Genre.of(GenreType.DRAMA, c.getCategory());
                Genre genre2 = Genre.of(GenreType.ROMANCE, c.getCategory());
                ContentGenre contentGenre1 = ContentGenre.of(content, genre1);
                ContentGenre contentGenre2 = ContentGenre.of(content, genre2);
                list.add(contentGenre1);
                list.add(contentGenre2);
            } else if (c.getCategory().getCategoryType().equals(CategoryType.MOVIE)) {
                Genre genre1 = Genre.of(GenreType.ACTION, c.getCategory());
                Genre genre2 = Genre.of(GenreType.COMEDY, c.getCategory());
                Genre genre3 = Genre.of(GenreType.ROMANCE, c.getCategory());
                ContentGenre contentGenre1 = ContentGenre.of(content, genre1);
                ContentGenre contentGenre2 = ContentGenre.of(content, genre2);
                ContentGenre contentGenre3 = ContentGenre.of(content, genre3);
                list.add(contentGenre1);
                list.add(contentGenre2);
                list.add(contentGenre3);
            } else if (c.getCategory().getCategoryType().equals(CategoryType.ANIMATION)) {
                Genre genre1 = Genre.of(GenreType.KIDS, c.getCategory());
                ContentGenre contentGenre1 = ContentGenre.of(content, genre1);
                list.add(contentGenre1);
            }
        });
        return list;
    }

}
