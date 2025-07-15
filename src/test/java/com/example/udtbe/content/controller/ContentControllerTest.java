package com.example.udtbe.content.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.fixture.CategoryFixture;
import com.example.udtbe.common.fixture.ContentCategoryFixture;
import com.example.udtbe.common.fixture.ContentCountryFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentGenreFixture;
import com.example.udtbe.common.fixture.ContentPlatformFixture;
import com.example.udtbe.common.fixture.CountryFixture;
import com.example.udtbe.common.fixture.GenreFixture;
import com.example.udtbe.common.fixture.PlatformFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.content.controller.ContentController;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.repository.CategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCountryRepository;
import com.example.udtbe.domain.content.repository.ContentGenreRepository;
import com.example.udtbe.domain.content.repository.ContentPlatformRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.CountryRepository;
import com.example.udtbe.domain.content.repository.GenreRepository;
import com.example.udtbe.domain.content.repository.PlatformRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ContentControllerTest extends ApiSupport {

    @Autowired
    ContentController contentController;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentPlatformRepository contentPlatformRepository;

    @Autowired
    ContentCountryRepository contentCountryRepository;

    @Autowired
    PlatformRepository platformRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ContentCategoryRepository contentCategoryRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    ContentGenreRepository contentGenreRepository;

    @AfterEach
    void tearDown() {
        contentPlatformRepository.deleteAllInBatch();
        contentCountryRepository.deleteAllInBatch();
        platformRepository.deleteAllInBatch();
        countryRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
    }

    @DisplayName("OTT 플랫폼, 국가, 방영일, 등급, 카테고리, 장르 등 필터링에 따른 콘텐츠 목록을 조회한다.")
    @Test
    void getFilteredContents_V1() throws Exception {
        // given
        final String koreaMovie = "한국영화";
        final String usaMovie = "미국영화";
        final String japanMovie = "일본영화";
        final String ratingAll = "전체 관람가";
        final String rating12 = "12세 이상";
        final String rating15 = "15세 이상";
        final String rating18 = "청소년 관람불가";
        final int year2020 = 2020;
        final int year2021 = 2021;
        final int year2022 = 2022;
        final int year2023 = 2023;
        final int year2024 = 2024;

        List<Country> savedCountries = countryRepository.saveAll(CountryFixture.countries());
        List<Platform> savedPlatforms = platformRepository.saveAll(PlatformFixture.platforms());
        List<Category> savedCategories = categoryRepository.saveAll(CategoryFixture.categories());
        List<Genre> savedGenres = genreRepository.saveAll(
                GenreFixture.genres(savedCategories.get(0)));

        List<Content> contents = new ArrayList<>();
        initContent(contents, koreaMovie + "1", year2020, ratingAll);
        initContent(contents, koreaMovie + "2", year2022, rating15);
        initContent(contents, koreaMovie + "3", year2021, rating18);
        initContent(contents, usaMovie + "1", year2024, rating15);
        initContent(contents, usaMovie + "2", year2023, rating15);
        initContent(contents, usaMovie + "3", year2021, rating12);
        initContent(contents, japanMovie + "1", year2024, rating18);
        initContent(contents, japanMovie + "2", year2024, ratingAll);
        initContent(contents, japanMovie + "3", year2022, rating12);

        List<Content> savedContents = contentRepository.saveAll(contents);

        List<ContentCountry> contentCountries = new ArrayList<>();
        initContentCountry(contentCountries, savedContents.get(0), savedCountries.get(0));
        initContentCountry(contentCountries, savedContents.get(1), savedCountries.get(0));
        initContentCountry(contentCountries, savedContents.get(2), savedCountries.get(0));
        initContentCountry(contentCountries, savedContents.get(3), savedCountries.get(3));
        initContentCountry(contentCountries, savedContents.get(4), savedCountries.get(3));
        initContentCountry(contentCountries, savedContents.get(5), savedCountries.get(3));
        initContentCountry(contentCountries, savedContents.get(6), savedCountries.get(1));
        initContentCountry(contentCountries, savedContents.get(7), savedCountries.get(1));
        initContentCountry(contentCountries, savedContents.get(8), savedCountries.get(1));
        contentCountryRepository.saveAll(contentCountries);

        List<ContentPlatform> contentPlatforms = new ArrayList<>();
        initContentPlatform(contentPlatforms, savedContents.get(0), savedPlatforms.get(0));
        initContentPlatform(contentPlatforms, savedContents.get(0), savedPlatforms.get(4));
        initContentPlatform(contentPlatforms, savedContents.get(1), savedPlatforms.get(1));
        initContentPlatform(contentPlatforms, savedContents.get(2), savedPlatforms.get(0));
        initContentPlatform(contentPlatforms, savedContents.get(2), savedPlatforms.get(3));
        initContentPlatform(contentPlatforms, savedContents.get(2), savedPlatforms.get(5));
        initContentPlatform(contentPlatforms, savedContents.get(3), savedPlatforms.get(1));
        initContentPlatform(contentPlatforms, savedContents.get(4), savedPlatforms.get(2));
        initContentPlatform(contentPlatforms, savedContents.get(5), savedPlatforms.get(4));
        initContentPlatform(contentPlatforms, savedContents.get(6), savedPlatforms.get(3));
        initContentPlatform(contentPlatforms, savedContents.get(7), savedPlatforms.get(5));
        initContentPlatform(contentPlatforms, savedContents.get(8), savedPlatforms.get(6));
        contentPlatformRepository.saveAll(contentPlatforms);

        List<ContentCategory> contentCategories = new ArrayList<>();
        initContentCategory(contentCategories, savedContents.get(0), savedCategories.get(0));
        initContentCategory(contentCategories, savedContents.get(1), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(2), savedCategories.get(2));
        initContentCategory(contentCategories, savedContents.get(3), savedCategories.get(3));
        initContentCategory(contentCategories, savedContents.get(4), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(5), savedCategories.get(0));
        initContentCategory(contentCategories, savedContents.get(6), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(7), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(8), savedCategories.get(0));
        contentCategoryRepository.saveAll(contentCategories);

        List<ContentGenre> contentGenres = new ArrayList<>();
        initContentGenre(contentGenres, savedContents.get(0), savedGenres.get(0));
        initContentGenre(contentGenres, savedContents.get(0), savedGenres.get(2));
        initContentGenre(contentGenres, savedContents.get(1), savedGenres.get(12));
        initContentGenre(contentGenres, savedContents.get(2), savedGenres.get(13));
        initContentGenre(contentGenres, savedContents.get(3), savedGenres.get(12));
        initContentGenre(contentGenres, savedContents.get(4), savedGenres.get(10));
        initContentGenre(contentGenres, savedContents.get(5), savedGenres.get(3));
        initContentGenre(contentGenres, savedContents.get(5), savedGenres.get(5));
        initContentGenre(contentGenres, savedContents.get(6), savedGenres.get(6));
        initContentGenre(contentGenres, savedContents.get(7), savedGenres.get(0));
        initContentGenre(contentGenres, savedContents.get(8), savedGenres.get(3));
        contentGenreRepository.saveAll(contentGenres);

        // when  // then
        mockMvc.perform(post("/api/contents")
                        .param("size", "3")
                        .param("contentSearchConditionDTO.platforms", "넷플릭스", "디즈니+")
                        .param("contentSearchConditionDTO.countries", "한국", "미국")
                        .param("contentSearchConditionDTO.openDates", "2020-01-01T00:00:00",
                                "2021-01-01T00:00:00")
                        .param("contentSearchConditionDTO.ratings", "전체 관람가", "12세 이상")
                        .param("contentSearchConditionDTO.categories", "영화")
                        .param("contentSearchConditionDTO.genres", "액션", "스릴러", "SF", "어드벤처")
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item").isArray())
                .andExpect(jsonPath("$.item.length()").value(2))
                .andExpect(jsonPath("$.item[0].title").value(usaMovie + "3"))
                .andExpect(jsonPath("$.item[1].title").value(koreaMovie + "1"))
                .andExpect(jsonPath("$.nextCursor", is(nullValue())))
                .andExpect(jsonPath("$.hasNext").value(false))
        ;
    }

    @DisplayName("OTT 플랫폼, 국가, 방영일, 등급, 카테고리, 장르 등 필터링에 따른 콘텐츠 목록을 조회한다.")
    @Test
    void getFilteredContents_V2() throws Exception {
        // given
        final String koreaMovie = "한국영화";
        final String usaMovie = "미국영화";
        final String japanMovie = "일본영화";
        final String ratingAll = "전체 관람가";
        final String rating12 = "12세 이상";
        final String rating15 = "15세 이상";
        final String rating18 = "청소년 관람불가";
        final int year2020 = 2020;
        final int year2021 = 2021;
        final int year2022 = 2022;
        final int year2023 = 2023;
        final int year2024 = 2024;

        List<Country> savedCountries = countryRepository.saveAll(CountryFixture.countries());
        List<Platform> savedPlatforms = platformRepository.saveAll(PlatformFixture.platforms());
        List<Category> savedCategories = categoryRepository.saveAll(CategoryFixture.categories());
        List<Genre> savedGenres = genreRepository.saveAll(
                GenreFixture.genres(savedCategories.get(0)));

        List<Content> contents = new ArrayList<>();
        initContent(contents, koreaMovie + "1", year2020, ratingAll);
        initContent(contents, koreaMovie + "2", year2022, rating15);
        initContent(contents, koreaMovie + "3", year2021, rating18);
        initContent(contents, usaMovie + "1", year2024, rating15);
        initContent(contents, usaMovie + "2", year2023, rating15);
        initContent(contents, usaMovie + "3", year2021, rating12);
        initContent(contents, japanMovie + "1", year2024, rating18);
        initContent(contents, japanMovie + "2", year2024, ratingAll);
        initContent(contents, japanMovie + "3", year2022, rating12);

        List<Content> savedContents = contentRepository.saveAll(contents);

        List<ContentCountry> contentCountries = new ArrayList<>();
        initContentCountry(contentCountries, savedContents.get(0), savedCountries.get(0));
        initContentCountry(contentCountries, savedContents.get(1), savedCountries.get(0));
        initContentCountry(contentCountries, savedContents.get(2), savedCountries.get(0));
        initContentCountry(contentCountries, savedContents.get(3), savedCountries.get(3));
        initContentCountry(contentCountries, savedContents.get(4), savedCountries.get(3));
        initContentCountry(contentCountries, savedContents.get(5), savedCountries.get(3));
        initContentCountry(contentCountries, savedContents.get(6), savedCountries.get(1));
        initContentCountry(contentCountries, savedContents.get(7), savedCountries.get(1));
        initContentCountry(contentCountries, savedContents.get(8), savedCountries.get(1));
        contentCountryRepository.saveAll(contentCountries);

        List<ContentPlatform> contentPlatforms = new ArrayList<>();
        initContentPlatform(contentPlatforms, savedContents.get(0), savedPlatforms.get(0));
        initContentPlatform(contentPlatforms, savedContents.get(0), savedPlatforms.get(4));
        initContentPlatform(contentPlatforms, savedContents.get(1), savedPlatforms.get(1));
        initContentPlatform(contentPlatforms, savedContents.get(2), savedPlatforms.get(0));
        initContentPlatform(contentPlatforms, savedContents.get(2), savedPlatforms.get(3));
        initContentPlatform(contentPlatforms, savedContents.get(2), savedPlatforms.get(5));
        initContentPlatform(contentPlatforms, savedContents.get(3), savedPlatforms.get(1));
        initContentPlatform(contentPlatforms, savedContents.get(4), savedPlatforms.get(2));
        initContentPlatform(contentPlatforms, savedContents.get(5), savedPlatforms.get(4));
        initContentPlatform(contentPlatforms, savedContents.get(6), savedPlatforms.get(3));
        initContentPlatform(contentPlatforms, savedContents.get(7), savedPlatforms.get(5));
        initContentPlatform(contentPlatforms, savedContents.get(8), savedPlatforms.get(6));
        contentPlatformRepository.saveAll(contentPlatforms);

        List<ContentCategory> contentCategories = new ArrayList<>();
        initContentCategory(contentCategories, savedContents.get(0), savedCategories.get(0));
        initContentCategory(contentCategories, savedContents.get(1), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(2), savedCategories.get(2));
        initContentCategory(contentCategories, savedContents.get(3), savedCategories.get(3));
        initContentCategory(contentCategories, savedContents.get(4), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(5), savedCategories.get(0));
        initContentCategory(contentCategories, savedContents.get(6), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(7), savedCategories.get(1));
        initContentCategory(contentCategories, savedContents.get(8), savedCategories.get(0));
        contentCategoryRepository.saveAll(contentCategories);

        List<ContentGenre> contentGenres = new ArrayList<>();
        initContentGenre(contentGenres, savedContents.get(0), savedGenres.get(0));
        initContentGenre(contentGenres, savedContents.get(0), savedGenres.get(2));
        initContentGenre(contentGenres, savedContents.get(1), savedGenres.get(12));
        initContentGenre(contentGenres, savedContents.get(2), savedGenres.get(13));
        initContentGenre(contentGenres, savedContents.get(3), savedGenres.get(12));
        initContentGenre(contentGenres, savedContents.get(4), savedGenres.get(10));
        initContentGenre(contentGenres, savedContents.get(5), savedGenres.get(3));
        initContentGenre(contentGenres, savedContents.get(5), savedGenres.get(5));
        initContentGenre(contentGenres, savedContents.get(6), savedGenres.get(6));
        initContentGenre(contentGenres, savedContents.get(7), savedGenres.get(0));
        initContentGenre(contentGenres, savedContents.get(8), savedGenres.get(3));
        contentGenreRepository.saveAll(contentGenres);

        // when  // then
        mockMvc.perform(post("/api/contents")
                        .param("size", "1")
                        .param("contentSearchConditionDTO.platforms", "넷플릭스", "디즈니+")
                        .param("contentSearchConditionDTO.countries", "한국", "미국")
                        .param("contentSearchConditionDTO.openDates", "2020-01-01T00:00:00",
                                "2021-01-01T00:00:00")
                        .param("contentSearchConditionDTO.ratings", "전체 관람가", "12세 이상")
                        .param("contentSearchConditionDTO.categories", "영화")
                        .param("contentSearchConditionDTO.genres", "액션", "스릴러", "SF", "어드벤처")
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item").isArray())
                .andExpect(jsonPath("$.item.length()").value(1))
                .andExpect(jsonPath("$.item[0].title").value(usaMovie + "3"))
                .andExpect(jsonPath("$.nextCursor").value(
                        savedContents.get(5).getId() + "|" + savedContents.get(5).getOpenDate())
                )
                .andExpect(jsonPath("$.hasNext").value(true))
        ;
    }

    private void initContent(List<Content> contents, String title, int year, String rating) {
        contents.add(ContentFixture.content(title, LocalDateTime.of(year, 1, 1, 0, 0), rating));
    }

    private void initContentCountry(List<ContentCountry> contentCountries,
            Content content, Country country) {
        contentCountries.add(ContentCountryFixture.contentCountry(content, country));
    }

    private void initContentPlatform(List<ContentPlatform> contentPlatforms,
            Content content, Platform platform) {
        contentPlatforms.add(ContentPlatformFixture.contentPlatform(content, platform));
    }

    private void initContentCategory(List<ContentCategory> contentCategories,
            Content content, Category category) {
        contentCategories.add(ContentCategoryFixture.contentCategory(content, category));
    }

    private void initContentGenre(List<ContentGenre> contentGenres,
            Content content, Genre genre) {
        contentGenres.add(ContentGenreFixture.contentGenre(content, genre));
    }
}
