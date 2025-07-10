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
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private EntityManager em;

    @BeforeEach
    void cleanDatabase() {
        metadataRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
        em.flush();
        em.clear();
    }

    @DisplayName("관리자는 콘텐츠를 저장한다.")
    @Test
    @Rollback
    void saveContent() {
        // given
        Content content = ContentFixture.content("해리포터", "빅잼");
        // when
        Content save = contentRepository.save(content);
        List<ContentCategory> contentCategories = defaultCategories(content);
        List<ContentPlatform> contentPlatforms = defaultPlatforms(content,3);
        List<ContentCountry> contentCountries = defaultCountries(content,3);
        List<ContentGenre> contentGenres = defaultGenres(contentCategories,content);
        List<ContentCast> contentCasts = defaultCasts(content,30);
        List<ContentDirector> contentDirectors = defaultDirectors(content,3);

        // then
        assertAll("연관관계까지 함께 저장 및 로딩",
                () -> assertThat(save.getId()).isNotNull(),
                () -> assertThat(save.getTitle()).isEqualTo(content.getTitle()),
                () -> assertThat(save.getContentPlatforms().size()).isEqualTo(contentPlatforms.size()),
                () -> assertThat(save.getContentDirectors().size()).isEqualTo(contentDirectors.size()),
                () -> assertThat(save.getContentCategories().size()).isEqualTo(contentCategories.size()),
                () -> assertThat(save.getContentCasts().size()).isEqualTo(contentCasts.size()),
                () -> assertThat(save.getContentCountries().size()).isEqualTo(contentCountries.size()),
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

        ContentMetadata contentMetadata = ContentFixture.contentMetadata("인터스텔라",content);
        // when
        ContentMetadata save = metadataRepository.save(contentMetadata);

        assertAll("ContentMetadata 저장 및 조회",
                () -> assertThat(save.getId()).isNotNull(),
                () -> assertThat(save.getContent().getId()).isEqualTo(contentMetadata.getId()),
                () -> assertThat(save.getTitle()).isEqualTo(contentMetadata.getTitle()),
                () -> assertThat(save.getRating()).isEqualTo(contentMetadata.getRating()),
                () -> assertThat(save.isDeleted()).isFalse(),
                () -> assertThat(save.getGenreTag()).hasSize(3),
                () -> {
                    assertNotNull(save.getGenreTag());
                    assertThat(save.getGenreTag().get(1)).isEqualTo(contentMetadata.getGenreTag().get(1));
                },
                () -> assertThat(save.getPlatformTag()).hasSize(3),
                () -> assertThat(save.getDirectorTag()).hasSize(2)
        );
    }

    @DisplayName("첫 페이지 조회: cursor=null, size=5")
    @Test
    @Rollback
    void findContentsAdminByCursor_firstPage_hasNextTrue() {
        // given
        List<Content> contents = ContentFixture.contents(10);
        contents.forEach(em::persist);
        em.flush();
        em.clear();
        int size = 5;
        Long lastId = contents.get(contents.size()-1).getId();

        // when
        CursorPageResponse<ContentDTO> page =
                contentRepository.findContentsAdminByCursor(null, size);

        // then
        List<ContentDTO> dtos = page.item();
        assertThat(dtos).hasSize(size)
                .extracting(ContentDTO::contentId)
                .containsExactly(IntStream.range(0, size).mapToObj(i -> lastId-i).toArray(Long[]::new));
        assertThat(page.hasNext()).isTrue();
        assertThat(page.nextCursor()).isEqualTo(String.valueOf(lastId-size+1));
    }


    private List<ContentPlatform> defaultPlatforms(Content content, int count) {
        List<ContentPlatform> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Platform p = Platform.of(PlatformType.NETFLIX);
            ContentPlatform cp = ContentPlatform.of("https://example.com/watch" + i, true);
            cp.addContentAndPlatform(content, p);
            list.add(cp);
        });
        return list;
    }

    private List<ContentCast> defaultCasts(Content content, int count) {
        List<ContentCast> list = new ArrayList<>();

        IntStream.rangeClosed(1,count).forEach(i -> {
            Cast c = Cast.of("박연진"+i, "https://example.com/cast"+i);
            ContentCast cc = ContentCast.of();
            cc.addContentAndCast(content, c);
            list.add(cc);
        });
        return list;
    }

    private List<ContentDirector> defaultDirectors(Content content, int count) {
        List<ContentDirector> list = new ArrayList<>();
        IntStream.rangeClosed(1,count).forEach(i -> {
            Director d = Director.of("감스트"+i);
            ContentDirector cd = ContentDirector.of();
            cd.addContentAndDirector(content, d);
            list.add(cd);
        });
        return list;
    }

    private List<ContentCountry> defaultCountries(Content content, int count) {
        List<ContentCountry> list = new ArrayList<>();

        IntStream.rangeClosed(1,count).forEach(i ->{
            Country c = Country.of("대한민국"+i);
            ContentCountry cc = ContentCountry.of();
            cc.addContentAndCountry(content, c);
            list.add(cc);
        });
        return list;
    }

    private List<ContentCategory> defaultCategories(Content content) {
        List<ContentCategory> list = new ArrayList<>();
        Category c = Category.of(CategoryType.DRAMA);
        Category c2 = Category.of(CategoryType.MOVIE);
        Category c3 = Category.of(CategoryType.ANIMATION);
        ContentCategory cc = ContentCategory.of();
        ContentCategory cc2 = ContentCategory.of();
        ContentCategory cc3 = ContentCategory.of();

        cc.addContentAndCategory(content, c);
        cc2.addContentAndCategory(content, c2);
        cc3.addContentAndCategory(content, c3);

        list.add(cc);
        list.add(cc2);
        list.add(cc3);
        return list;
    }

    private List<ContentGenre> defaultGenres(List<ContentCategory> contentCategories, Content content) {
        List<ContentGenre> list = new ArrayList<>();

        contentCategories.forEach(c -> {
            if(c.getCategory().getCategoryType().equals(CategoryType.DRAMA)){
                Genre g = Genre.of(GenreType.DRAMA,c.getCategory());
                Genre g2 = Genre.of(GenreType.ROMANCE,c.getCategory());
                ContentGenre cg = ContentGenre.of();
                ContentGenre cg2 = ContentGenre.of();
                cg.addContentAndGenre(content, g);
                cg2.addContentAndGenre(content, g2);
                list.add(cg);
                list.add(cg2);
            }
            else if(c.getCategory().getCategoryType().equals(CategoryType.MOVIE)){
                Genre g = Genre.of(GenreType.ACTION,c.getCategory());
                Genre g2 = Genre.of(GenreType.COMEDY,c.getCategory());
                Genre g3 = Genre.of(GenreType.ROMANCE,c.getCategory());
                ContentGenre cg = ContentGenre.of();
                ContentGenre cg2 = ContentGenre.of();
                ContentGenre cg3 = ContentGenre.of();
                cg.addContentAndGenre(content, g);
                cg2.addContentAndGenre(content, g2);
                cg3.addContentAndGenre(content, g3);
                list.add(cg);
                list.add(cg2);
                list.add(cg3);
            }
            else if(c.getCategory().getCategoryType().equals(CategoryType.ANIMATION)){
                Genre g = Genre.of(GenreType.KIDS,c.getCategory());
                ContentGenre cg = ContentGenre.of();
                cg.addContentAndGenre(content, g);
                list.add(cg);
            }
        });
        return list;
    }

}
