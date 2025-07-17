package com.example.udtbe.content.controller;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.fixture.CastFixture;
import com.example.udtbe.common.fixture.CategoryFixture;
import com.example.udtbe.common.fixture.ContentCastFixture;
import com.example.udtbe.common.fixture.ContentCategoryFixture;
import com.example.udtbe.common.fixture.ContentCountryFixture;
import com.example.udtbe.common.fixture.ContentDirectorFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentGenreFixture;
import com.example.udtbe.common.fixture.ContentPlatformFixture;
import com.example.udtbe.common.fixture.CountryFixture;
import com.example.udtbe.common.fixture.DirectorFixture;
import com.example.udtbe.common.fixture.GenreFixture;
import com.example.udtbe.common.fixture.PlatformFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.content.controller.ContentController;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.repository.CastRepository;
import com.example.udtbe.domain.content.repository.CategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCastRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCountryRepository;
import com.example.udtbe.domain.content.repository.ContentDirectorRepository;
import com.example.udtbe.domain.content.repository.ContentGenreRepository;
import com.example.udtbe.domain.content.repository.ContentPlatformRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.CountryRepository;
import com.example.udtbe.domain.content.repository.DirectorRepository;
import com.example.udtbe.domain.content.repository.GenreRepository;
import com.example.udtbe.domain.content.repository.PlatformRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Autowired
    CastRepository castRepository;

    @Autowired
    ContentCastRepository contentCastRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    ContentDirectorRepository contentDirectorRepository;

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
        mockMvc.perform(get("/api/contents")
                        .param("size", "3")
                        .param("platforms", "넷플릭스", "디즈니+")
                        .param("countries", "한국", "미국")
                        .param("openDates", "2020-01-01T00:00:00", "2021-01-01T00:00:00")
                        .param("ratings", "전체 관람가", "12세 이상")
                        .param("categories", "영화")
                        .param("genres", "액션", "스릴러", "SF", "어드벤처")
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
        mockMvc.perform(get("/api/contents")
                        .param("size", "1")
                        .param("platforms", "넷플릭스", "디즈니+")
                        .param("countries", "한국", "미국")
                        .param("openDates", "2020-01-01T00:00:00", "2021-01-01T00:00:00")
                        .param("ratings", "전체 관람가", "12세 이상")
                        .param("categories", "영화")
                        .param("genres", "액션", "스릴러", "SF", "어드벤처")
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

    @DisplayName("콘텐츠의 상세 정보를 조회한다.")
    @Test
    void getContentDetails() throws Exception {
        // given
        final String title = "세부 콘텐츠 조회";
        final LocalDateTime openDate = LocalDateTime.of(2025, 7, 15, 14, 0);
        final String rating = "전체 관람가";
        Content savedContent = contentRepository.save(
                ContentFixture.content(title, openDate, rating));

        List<Platform> savedPlatforms = platformRepository.saveAll(PlatformFixture.platforms());
        List<ContentPlatform> contentPlatforms = new ArrayList<>();
        initContentPlatform(contentPlatforms, savedContent, savedPlatforms.get(3));
        initContentPlatform(contentPlatforms, savedContent, savedPlatforms.get(6));
        contentPlatformRepository.saveAll(contentPlatforms);

        List<Cast> savedCasts = castRepository.saveAll(CastFixture.casts());
        List<ContentCast> contentCasts = new ArrayList<>();
        initContentCasts(contentCasts, savedContent, savedCasts.get(0));
        initContentCasts(contentCasts, savedContent, savedCasts.get(1));
        initContentCasts(contentCasts, savedContent, savedCasts.get(2));
        contentCastRepository.saveAll(contentCasts);

        List<Director> savedDirector = directorRepository.saveAll(DirectorFixture.directors());
        List<ContentDirector> contentDirectors = new ArrayList<>();
        initContentDirectors(contentDirectors, savedContent, savedDirector.get(3));
        contentDirectorRepository.saveAll(contentDirectors);

        List<Country> savedCountries = countryRepository.saveAll(CountryFixture.countries());
        List<ContentCountry> contentCountries = new ArrayList<>();
        initContentCountry(contentCountries, savedContent, savedCountries.get(0));
        initContentCountry(contentCountries, savedContent, savedCountries.get(3));
        contentCountryRepository.saveAll(contentCountries);

        List<Category> savedCategories = categoryRepository.saveAll(CategoryFixture.categories());
        List<ContentCategory> contentCategories = new ArrayList<>();
        initContentCategory(contentCategories, savedContent, savedCategories.get(0));
        contentCategoryRepository.saveAll(contentCategories);

        List<Genre> savedGenres = genreRepository.saveAll(
                GenreFixture.genres(savedCategories.get(0)));
        List<ContentGenre> contentGenres = new ArrayList<>();
        initContentGenre(contentGenres, savedContent, savedGenres.get(0));
        initContentGenre(contentGenres, savedContent, savedGenres.get(1));
        initContentGenre(contentGenres, savedContent, savedGenres.get(2));
        contentGenreRepository.saveAll(contentGenres);

        // when  // then
        mockMvc.perform(get("/api/contents/{contentId}", savedContent.getId())
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentId").value(savedContent.getId()))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.openDate", startsWith(openDate.toString())))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.platforms[0].platformType").value("웨이브"))
                .andExpect(jsonPath("$.platforms[1].platformType").value("Apple TV"))
                .andExpect(jsonPath("$.casts[0].castName").value("마동석"))
                .andExpect(jsonPath("$.casts[1].castName").value("황정민"))
                .andExpect(jsonPath("$.casts[2].castName").value("토니스타크"))
                .andExpect(jsonPath("$.directors", contains("김원석")))
                .andExpect(jsonPath("$.countries", contains("한국", "미국")))
                .andExpect(jsonPath("$.categories", contains("영화")))
                .andExpect(jsonPath("$.genres", contains("액션", "판타지", "SF")))
        ;
    }

    @DisplayName("존재하지 않은 콘텐츠를 조회할 수 없다.")
    @Test
    void throwExceptionWhenContentIsNotExist() throws Exception {

        Content content = ContentFixture.content("존재하지 않는 콘텐츠", "존재하지 않는 콘텐츠입니다.");
        ReflectionTestUtils.setField(content, "id", 100L);

        // when  // then
        mockMvc.perform(get("/api/contents/{contentId}", content.getId())
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CONTENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("콘텐츠를 찾을 수 없습니다."))
        ;
    }

    @DisplayName("요일별 추천 콘텐츠 목록을 조회한다.")
    @Test
    void getWeeklyRecommendedContents() throws Exception {
        // given

        List<Content> contents = new ArrayList<>();
        contents.add(ContentFixture.content("버라이어티", "버라이어티"));
        contents.add(ContentFixture.content("코미디", "코미디"));
        contents.add(ContentFixture.content("액션", "액션"));
        contents.add(ContentFixture.content("어드벤처", "어드벤처"));
        contents.add(ContentFixture.content("범죄", "범죄"));
        contents.add(ContentFixture.content("스릴러", "스릴러"));
        contents.add(ContentFixture.content("멜로/로맨스", "멜로/로맨스"));
        contents.add(ContentFixture.content("다큐멘터리", "다큐멘터리"));

        List<Category> savedCategories = categoryRepository.saveAll(CategoryFixture.categories());
        List<Content> savedContents = contentRepository.saveAll(contents);
        List<Genre> savedGenres = genreRepository.saveAll(
                GenreFixture.genres(savedCategories.get(0)));

        List<ContentGenre> contentGenres = new ArrayList<>();
        initContentGenre(contentGenres, savedContents.get(0), savedGenres.get(19));
        initContentGenre(contentGenres, savedContents.get(1), savedGenres.get(7));
        initContentGenre(contentGenres, savedContents.get(2), savedGenres.get(0));
        initContentGenre(contentGenres, savedContents.get(3), savedGenres.get(6));
        initContentGenre(contentGenres, savedContents.get(4), savedGenres.get(14));
        initContentGenre(contentGenres, savedContents.get(5), savedGenres.get(3));
        initContentGenre(contentGenres, savedContents.get(6), savedGenres.get(9));
        initContentGenre(contentGenres, savedContents.get(7), savedGenres.get(13));
        contentGenreRepository.saveAll(contentGenres);

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        Map<DayOfWeek, List<Long>> expectedContentIdsByGenre = Map.of(
                MONDAY, List.of(savedContents.get(1).getId(), savedContents.get(0).getId()),
                TUESDAY, List.of(savedContents.get(3).getId(), savedContents.get(2).getId()),
                WEDNESDAY, List.of(savedContents.get(5).getId(), savedContents.get(4).getId()),
                FRIDAY, List.of(savedContents.get(6).getId()),
                SATURDAY, List.of(savedContents.get(7).getId())
        );

        List<Long> expectedIds = expectedContentIdsByGenre.getOrDefault(today, List.of());

        // when  // then
        if (today == DayOfWeek.THURSDAY || today == DayOfWeek.SUNDAY) {
            mockMvc.perform(get("/api/contents/weekly")
                            .param("size", "4")
                            .contentType(APPLICATION_JSON)
                            .cookie(accessTokenOfMember)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].length()").value(4))
            ;
        } else {
            mockMvc.perform(get("/api/contents/weekly")
                            .param("size", "2")
                            .contentType(APPLICATION_JSON)
                            .cookie(accessTokenOfMember)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].contentId", containsInAnyOrder(
                            expectedIds.stream()
                                    .map(Long::intValue)
                                    .toArray()
                    )));
        }
    }

    private void initContentDirectors(List<ContentDirector> contentDirectors, Content content,
            Director director) {
        contentDirectors.add(ContentDirectorFixture.contentDirector(content, director));
    }

    private void initContentCasts(List<ContentCast> contentCasts, Content content,
            Cast cast) {
        contentCasts.add(ContentCastFixture.contentCast(content, cast));
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
