package com.example.udtbe.content.controller;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.fixture.ContentCastFixture;
import com.example.udtbe.common.fixture.ContentCategoryFixture;
import com.example.udtbe.common.fixture.ContentCountryFixture;
import com.example.udtbe.common.fixture.ContentDirectorFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentGenreFixture;
import com.example.udtbe.common.fixture.ContentPlatformFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Sql(scripts = {
        "classpath:cast-test.sql",
        "classpath:category-test.sql",
        "classpath:country-test.sql",
        "classpath:director-test.sql",
        "classpath:genre-test.sql",
        "classpath:platform-test.sql",
},
        executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class ContentControllerTest extends ApiSupport {

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

    @Autowired
    PlatformTransactionManager tm;

    @AfterEach
    void tearDown() {
        contentPlatformRepository.deleteAllInBatch();
        contentCountryRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
    }

    @DisplayName("OTT 플랫폼, 국가, 방영일, 등급, 카테고리, 장르 등 필터링에 따른 콘텐츠 목록을 조회한다.")
    @Test
    void getFilteredContents_V1() throws Exception {
        // given
        final String kor = "한국";
        final String usa = "미국";
        final String jap = "일본";
        final String koreaMovie = "한국영화";
        final String usaMovie = "미국영화";
        final String japanMovie = "일본영화";
        final String ratingAll = "전체 관람가";
        final String rating12 = "12세 이상";
        final String rating15 = "15세 이상";
        final int year2020 = 2020;
        final int year2021 = 2021;

        TransactionTemplate tx = new TransactionTemplate(tm);
        tx.execute(status -> {
            Country KR = countryRepository.findByCountryName(kor).orElseThrow();
            Country US = countryRepository.findByCountryName(usa).orElseThrow();
            Country JP = countryRepository.findByCountryName(jap).orElseThrow();

            Platform NETFLIX = platformRepository.findByPlatformType(PlatformType.NETFLIX)
                    .orElseThrow();
            Platform DISNEY_PLUS = platformRepository.findByPlatformType(PlatformType.DISNEY_PLUS)
                    .orElseThrow();
            Category MOVIE = categoryRepository.findByCategoryType(CategoryType.MOVIE)
                    .orElseThrow();

            Genre ACTION = genreRepository.findByGenreTypeAndCategory(GenreType.ACTION, MOVIE)
                    .orElseThrow();
            Genre SF = genreRepository.findByGenreTypeAndCategory(GenreType.SF, MOVIE)
                    .orElseThrow();
            Genre ADVENTURE = genreRepository.findByGenreTypeAndCategory(GenreType.ADVENTURE, MOVIE)
                    .orElseThrow();

            Content in1 = contentRepository.save(ContentFixture.content(
                    usaMovie + "3", LocalDateTime.of(year2021, 1, 1, 0, 0), rating12)
            );
            Content in2 = contentRepository.save(ContentFixture.content(
                    koreaMovie + "1", LocalDateTime.of(year2020, 1, 1, 0, 0), ratingAll)
            );
            Content out1 = contentRepository.save(ContentFixture.content(
                    japanMovie + "1", LocalDateTime.of(year2021, 1, 1, 0, 0), rating12)
            );
            Content out2 = contentRepository.save(ContentFixture.content(
                    usaMovie + "1", LocalDateTime.of(year2021, 1, 1, 0, 0), rating15)
            );

            contentCountryRepository.saveAll(
                    List.of(
                            ContentCountryFixture.contentCountry(in1, US),
                            ContentCountryFixture.contentCountry(in2, KR),
                            ContentCountryFixture.contentCountry(out1, JP),
                            ContentCountryFixture.contentCountry(out2, US)
                    )
            );

            contentPlatformRepository.saveAll(
                    List.of(
                            ContentPlatformFixture.contentPlatform(in1, DISNEY_PLUS),
                            ContentPlatformFixture.contentPlatform(in2, NETFLIX),
                            ContentPlatformFixture.contentPlatform(out1, NETFLIX),
                            ContentPlatformFixture.contentPlatform(out2, NETFLIX)
                    )
            );

            contentCategoryRepository.saveAll(
                    List.of(
                            ContentCategoryFixture.contentCategory(in1, MOVIE),
                            ContentCategoryFixture.contentCategory(in2, MOVIE),
                            ContentCategoryFixture.contentCategory(out1, MOVIE),
                            ContentCategoryFixture.contentCategory(out2, MOVIE)
                    )
            );

            contentGenreRepository.saveAll(
                    List.of(
                            ContentGenreFixture.contentGenre(in1, ACTION),
                            ContentGenreFixture.contentGenre(in2, SF),
                            ContentGenreFixture.contentGenre(out1, ACTION),
                            ContentGenreFixture.contentGenre(out2, ADVENTURE)
                    )
            );

            return null;
        });

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
        final String kor = "한국";
        final String usa = "미국";
        final String jap = "일본";
        final String koreaMovie = "한국영화";
        final String usaMovie = "미국영화";
        final String japanMovie = "일본영화";
        final String ratingAll = "전체 관람가";
        final String rating12 = "12세 이상";
        final String rating15 = "15세 이상";
        final int year2020 = 2020;
        final int year2021 = 2021;
        final Content[] holder = new Content[2];

        TransactionTemplate tx = new TransactionTemplate(tm);
        tx.execute(status -> {
            Country KR = countryRepository.findByCountryName(kor).orElseThrow();
            Country US = countryRepository.findByCountryName(usa).orElseThrow();
            Country JP = countryRepository.findByCountryName(jap).orElseThrow();

            Platform NETFLIX = platformRepository.findByPlatformType(PlatformType.NETFLIX)
                    .orElseThrow();
            Platform DISNEY_PLUS = platformRepository.findByPlatformType(PlatformType.DISNEY_PLUS)
                    .orElseThrow();
            Category MOVIE = categoryRepository.findByCategoryType(CategoryType.MOVIE)
                    .orElseThrow();

            Genre ACTION = genreRepository.findByGenreTypeAndCategory(GenreType.ACTION, MOVIE)
                    .orElseThrow();
            Genre SF = genreRepository.findByGenreTypeAndCategory(GenreType.SF, MOVIE)
                    .orElseThrow();
            Genre ADVENTURE = genreRepository.findByGenreTypeAndCategory(GenreType.ADVENTURE, MOVIE)
                    .orElseThrow();

            Content in1 = contentRepository.save(ContentFixture.content(
                    usaMovie + "3", LocalDateTime.of(year2021, 1, 1, 0, 0), rating12)
            );
            Content in2 = contentRepository.save(ContentFixture.content(
                    koreaMovie + "1", LocalDateTime.of(year2020, 1, 1, 0, 0), ratingAll)
            );
            Content out1 = contentRepository.save(ContentFixture.content(
                    japanMovie + "1", LocalDateTime.of(year2021, 1, 1, 0, 0), rating12)
            );
            Content out2 = contentRepository.save(ContentFixture.content(
                    usaMovie + "1", LocalDateTime.of(year2021, 1, 1, 0, 0), rating15)
            );

            holder[0] = in1;
            holder[1] = in2;

            contentCountryRepository.saveAll(
                    List.of(
                            ContentCountryFixture.contentCountry(in1, US),
                            ContentCountryFixture.contentCountry(in2, KR),
                            ContentCountryFixture.contentCountry(out1, JP),
                            ContentCountryFixture.contentCountry(out2, US)
                    )
            );

            contentPlatformRepository.saveAll(
                    List.of(
                            ContentPlatformFixture.contentPlatform(in1, DISNEY_PLUS),
                            ContentPlatformFixture.contentPlatform(in2, NETFLIX),
                            ContentPlatformFixture.contentPlatform(out1, NETFLIX),
                            ContentPlatformFixture.contentPlatform(out2, NETFLIX)
                    )
            );

            contentCategoryRepository.saveAll(
                    List.of(
                            ContentCategoryFixture.contentCategory(in1, MOVIE),
                            ContentCategoryFixture.contentCategory(in2, MOVIE),
                            ContentCategoryFixture.contentCategory(out1, MOVIE),
                            ContentCategoryFixture.contentCategory(out2, MOVIE)
                    )
            );

            contentGenreRepository.saveAll(
                    List.of(
                            ContentGenreFixture.contentGenre(in1, ACTION),
                            ContentGenreFixture.contentGenre(in2, SF),
                            ContentGenreFixture.contentGenre(out1, ACTION),
                            ContentGenreFixture.contentGenre(out2, ADVENTURE)
                    )
            );

            return null;
        });

        String expectedNextCursor = holder[0].getId() + "|" + holder[0].getOpenDate();

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
                .andExpect(jsonPath("$.nextCursor").value(expectedNextCursor))
                .andExpect(jsonPath("$.hasNext").value(true))
        ;
    }

    @DisplayName("콘텐츠의 상세 정보를 조회한다.")
    @Test
    void getContentDetails() throws Exception {
        // given
        final String kor = "한국";
        final String usa = "미국";
        final String koreaMovie = "한국영화";
        final String usaMovie = "미국영화";
        final String rating12 = "12세 이상";
        final int year2020 = 2020;
        final Content[] holder = new Content[2];

        TransactionTemplate tx = new TransactionTemplate(tm);
        tx.execute(status -> {
            Country KR = countryRepository.findByCountryName(kor).orElseThrow();
            Country US = countryRepository.findByCountryName(usa).orElseThrow();

            Platform NETFLIX = platformRepository.findByPlatformType(PlatformType.NETFLIX)
                    .orElseThrow();
            Platform DISNEY_PLUS = platformRepository.findByPlatformType(PlatformType.DISNEY_PLUS)
                    .orElseThrow();
            Category MOVIE = categoryRepository.findByCategoryType(CategoryType.MOVIE)
                    .orElseThrow();
            Genre ACTION = genreRepository.findByGenreTypeAndCategory(GenreType.ACTION, MOVIE)
                    .orElseThrow();

            Cast cast01 = castRepository.findByCastNameAndCastImageUrl("출연진01",
                    "https://example.com/images/cast-01.jpg").orElseThrow();
            Cast cast02 = castRepository.findByCastNameAndCastImageUrl("출연진02",
                    "https://example.com/images/cast-02.jpg").orElseThrow();

            Director director01 = directorRepository.findByDirectorName("감독01").orElseThrow();
            Director director02 = directorRepository.findByDirectorName("감독02").orElseThrow();

            Content in = contentRepository.save(ContentFixture.content(
                    koreaMovie, LocalDateTime.of(year2020, 1, 1, 0, 0), rating12)
            );
            Content out = contentRepository.save(ContentFixture.content(
                    usaMovie, LocalDateTime.of(year2020, 1, 1, 0, 0), rating12)
            );

            holder[0] = in;
            holder[1] = out;

            contentCountryRepository.saveAll(
                    List.of(
                            ContentCountryFixture.contentCountry(in, KR),
                            ContentCountryFixture.contentCountry(out, US)
                    )
            );

            contentPlatformRepository.saveAll(
                    List.of(
                            ContentPlatformFixture.contentPlatform(in, DISNEY_PLUS),
                            ContentPlatformFixture.contentPlatform(out, NETFLIX)
                    )
            );

            contentCategoryRepository.saveAll(
                    List.of(
                            ContentCategoryFixture.contentCategory(in, MOVIE),
                            ContentCategoryFixture.contentCategory(out, MOVIE)
                    )
            );

            contentGenreRepository.saveAll(
                    List.of(
                            ContentGenreFixture.contentGenre(in, ACTION),
                            ContentGenreFixture.contentGenre(out, ACTION)
                    )
            );

            contentCastRepository.saveAll(
                    List.of(
                            ContentCastFixture.contentCast(in, cast01),
                            ContentCastFixture.contentCast(out, cast02)
                    )
            );

            contentDirectorRepository.saveAll(
                    List.of(
                            ContentDirectorFixture.contentDirector(in, director01),
                            ContentDirectorFixture.contentDirector(out, director02)
                    )
            );

            return null;
        });

        // when  // then
        mockMvc.perform(get("/api/contents/{contentId}", holder[0].getId())
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentId").value(holder[0].getId()))
                .andExpect(jsonPath("$.title").value(koreaMovie))
                .andExpect(jsonPath("$.openDate", startsWith(String.valueOf(year2020))))
                .andExpect(jsonPath("$.rating").value(rating12))
                .andExpect(jsonPath("$.platforms[0].platformType").value("디즈니+"))
                .andExpect(jsonPath("$.casts[0].castName").value("출연진01"))
                .andExpect(jsonPath("$.directors", contains("감독01")))
                .andExpect(jsonPath("$.countries", contains("한국")))
                .andExpect(jsonPath("$.categories", contains("영화")))
                .andExpect(jsonPath("$.genres", contains("액션")))
        ;
    }

    @DisplayName("존재하지 않은 콘텐츠를 조회할 수 없다.")
    @Test
    void throwExceptionWhenContentIsNotExist() throws Exception {
        final Long contentId = 1L;

        // when  // then
        mockMvc.perform(get("/api/contents/{contentId}", contentId)
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CONTENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("콘텐츠를 찾을 수 없습니다."))
        ;
    }

    @DisplayName("요일별 추천 콘텐츠 목록을 조회한다.")
    @Test
    void getWeeklyRecommendedContents() throws Exception {
        // given
        final Content[] holder = new Content[7];

        TransactionTemplate tx = new TransactionTemplate(tm);
        tx.execute(status -> {
            Content in1 = contentRepository.save(ContentFixture.content("버라이어티", "버라이어티"));
            Content in2 = contentRepository.save(ContentFixture.content("코미디", "코미디"));
            Content in3 = contentRepository.save(ContentFixture.content("어드벤처", "어드벤처"));
            Content in4 = contentRepository.save(ContentFixture.content("범죄", "범죄"));
            Content in5 = contentRepository.save(ContentFixture.content("스릴러", "스릴러"));
            Content in6 = contentRepository.save(ContentFixture.content("멜로/로맨스", "멜로/로맨스"));
            Content in7 = contentRepository.save(ContentFixture.content("다큐멘터리", "다큐멘터리"));

            Category MOVIE = categoryRepository.findByCategoryType(CategoryType.MOVIE)
                    .orElseThrow();
            Category DRAMA = categoryRepository.findByCategoryType(CategoryType.DRAMA)
                    .orElseThrow();

            Genre VARIETY2 = genreRepository.findByGenreTypeAndCategory(GenreType.VARIETY, DRAMA)
                    .orElseThrow();
            Genre COMEDY = genreRepository.findByGenreTypeAndCategory(GenreType.COMEDY, MOVIE)
                    .orElseThrow();
            Genre ADVENTURE = genreRepository.findByGenreTypeAndCategory(GenreType.ADVENTURE, MOVIE)
                    .orElseThrow();
            Genre CRIME = genreRepository.findByGenreTypeAndCategory(GenreType.CRIME, MOVIE)
                    .orElseThrow();
            Genre THRILLER = genreRepository.findByGenreTypeAndCategory(GenreType.THRILLER, MOVIE)
                    .orElseThrow();
            Genre ROMANCE = genreRepository.findByGenreTypeAndCategory(GenreType.ROMANCE, MOVIE)
                    .orElseThrow();
            Genre DOCUMENTARY = genreRepository.findByGenreTypeAndCategory(GenreType.DOCUMENTARY,
                            MOVIE)
                    .orElseThrow();

            contentGenreRepository.saveAll(
                    List.of(
                            ContentGenreFixture.contentGenre(in1, VARIETY2),
                            ContentGenreFixture.contentGenre(in2, COMEDY),
                            ContentGenreFixture.contentGenre(in3, ADVENTURE),
                            ContentGenreFixture.contentGenre(in4, CRIME),
                            ContentGenreFixture.contentGenre(in5, THRILLER),
                            ContentGenreFixture.contentGenre(in6, ROMANCE),
                            ContentGenreFixture.contentGenre(in7, DOCUMENTARY)
                    )
            );

            holder[0] = in1;
            holder[1] = in2;
            holder[2] = in3;
            holder[3] = in4;
            holder[4] = in5;
            holder[5] = in6;
            holder[6] = in7;

            return null;
        });

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        Map<DayOfWeek, List<Long>> expectedContentIdsByGenre = Map.of(
                MONDAY, List.of(holder[0].getId(), holder[1].getId()),
                TUESDAY, List.of(holder[2].getId()),
                WEDNESDAY, List.of(holder[3].getId(), holder[4].getId()),
                FRIDAY, List.of(holder[5].getId()),
                SATURDAY, List.of(holder[6].getId())
        );

        List<Long> expectedIds = expectedContentIdsByGenre.getOrDefault(today, List.of());

        // when  // then
        if (today == DayOfWeek.THURSDAY || today == DayOfWeek.SUNDAY) {
            mockMvc.perform(get("/api/contents/weekly")
                            .param("size", "4")
                            .contentType(APPLICATION_JSON)
                            .cookie(accessTokenOfMember)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(4))
            ;
        } else {
            mockMvc.perform(get("/api/contents/weekly")
                            .param("size", "2")
                            .contentType(APPLICATION_JSON)
                            .cookie(accessTokenOfMember)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].contentId", containsInAnyOrder(
                            expectedIds.stream()
                                    .map(Long::intValue)
                                    .toArray()
                    )))
            ;
        }
    }

    @DisplayName("최신 콘텐츠 목록을 조회할 수 있다.")
    @Test
    void getRecentContents() throws Exception {
        // given
        final String koreaMovie = "한국영화";
        final String rating12 = "12세 이상";
        final int year2020 = 2020;
        final int year2021 = 2021;
        final int year2022 = 2021;
        final Content[] holder = new Content[3];

        TransactionTemplate tx = new TransactionTemplate(tm);
        tx.execute(status -> {
            Content in1 = contentRepository.save(contentRepository.save(ContentFixture.content(
                    koreaMovie + "1", LocalDateTime.of(year2020, 1, 1, 0, 0), rating12)
            ));
            Content in2 = contentRepository.save(contentRepository.save(ContentFixture.content(
                    koreaMovie + "2", LocalDateTime.of(year2021, 1, 1, 0, 0), rating12)
            ));
            Content in3 = contentRepository.save(contentRepository.save(ContentFixture.content(
                    koreaMovie + "3", LocalDateTime.of(year2022, 1, 1, 0, 0), rating12)
            ));

            holder[0] = in1;
            holder[1] = in2;
            holder[2] = in3;

            return null;
        });

        // when // then
        mockMvc.perform(get("/api/contents/recent")
                        .param("size", "2")
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfMember))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(koreaMovie + "3"))
                .andExpect(jsonPath("$[1].title").value(koreaMovie + "2"))
        ;
    }
}
