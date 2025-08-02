package com.example.udtbe.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.fixture.AdminContentDeleteJobFixture;
import com.example.udtbe.common.fixture.CastFixture;
import com.example.udtbe.common.fixture.ContentCategoryFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.DirectorFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.admin.dto.common.AdminCastDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminDirectorDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsRegisterRequest;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.repository.CastRepository;
import com.example.udtbe.domain.content.repository.CategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCastRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCountryRepository;
import com.example.udtbe.domain.content.repository.ContentDirectorRepository;
import com.example.udtbe.domain.content.repository.ContentGenreRepository;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentPlatformRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.DirectorRepository;
import com.example.udtbe.domain.content.repository.GenreRepository;
import com.example.udtbe.domain.content.repository.PlatformRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "classpath:data-test.sql")
public class AdminControllerTest extends ApiSupport {

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ContentMetadataRepository contentMetadataRepository;
    @Autowired
    private ContentCategoryRepository contentCategoryRepository;
    @Autowired
    private ContentGenreRepository contentGenreRepository;
    @Autowired
    private ContentCastRepository contentCastRepository;
    @Autowired
    private ContentDirectorRepository contentDirectorRepository;
    @Autowired
    private ContentPlatformRepository contentPlatformRepository;
    @Autowired
    private ContentCountryRepository contentCountryRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private CastRepository castRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private AdminContentDeleteJobRepository adminContentDeleteJobRepository;

    @AfterEach
    void tearDown() {
        contentCategoryRepository.deleteAllInBatch();
        contentGenreRepository.deleteAllInBatch();
        contentCastRepository.deleteAllInBatch();
        contentDirectorRepository.deleteAllInBatch();
        contentPlatformRepository.deleteAllInBatch();
        contentCountryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        genreRepository.deleteAllInBatch();
        platformRepository.deleteAllInBatch();
        castRepository.deleteAllInBatch();
        directorRepository.deleteAllInBatch();
        contentMetadataRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("콘텐츠 등록 시 배치 목록 DB에 저장할 수 있다.")
    void contentRegister() throws Exception {
        // when & then
        AdminContentRegisterRequest adminContentRegisterRequest = new AdminContentRegisterRequest(
                "테스트 제목", "테스트 설명",
                "https://poster", "https://backdrop", "https://trailer",
                LocalDateTime.of(2025, 7, 11, 0, 0),
                100, 1, "전체관람가",
                List.of(new AdminCategoryDTO("영화", List.of("액션"))),
                List.of("한국"), List.of(1L, 2L),
                List.of(1L, 2L),
                List.of(new AdminPlatformDTO("넷플릭스", "https://watch"))
        );

        mockMvc.perform(post("/api/admin/contents/registerjob")
                        .content(objectMapper.writeValueAsString(adminContentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registerJobId").value(1L))
                .andReturn();
    }

    @Test
    @DisplayName("콘텐츠 수정 시 배치 목록 DB에 저장할 수 있다.")
    void updateContent() throws Exception {

        Content content = ContentFixture.content("테스트 제목", "테스트 설명");
        contentRepository.save(content);

        // given
        AdminContentUpdateRequest adminContentUpdateRequest = new AdminContentUpdateRequest(
                "수정 테스트 제목", "수정 테스트 설명",
                "p1", "b1", "t1",
                LocalDateTime.of(2025, 7, 11, 0, 0),
                90, 1, "12세",
                List.of(
                        new AdminCategoryDTO("애니메이션", List.of("키즈")),
                        new AdminCategoryDTO("드라마", List.of("서사/드라마"))
                ),
                List.of("한국"), List.of(1L, 2L),
                List.of(1L, 2L),
                List.of(
                        new AdminPlatformDTO("넷플릭스", "w1"),
                        new AdminPlatformDTO("디즈니+", "w2")
                )
        );
        mockMvc.perform(post("/api/admin/contents/updatejob/{contentId}", content.getId())
                        .content(objectMapper.writeValueAsString(adminContentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.updateJobId").value(1L))
                .andReturn();
    }

    @Test
    @DisplayName("콘텐츠 삭제 시 배치 목록 DB에 저장할 수 있다.")
    void deleteContent() throws Exception {

        // given
        Content content = ContentFixture.content("존재하지 않는 콘텐츠", "x");
        contentRepository.save(content);

        // when
        mockMvc.perform(post("/api/admin/contents/deletejob/{contentId}", content.getId())
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deleteJobId").value(1L))
                .andReturn();
    }


    @Test
    @DisplayName("페이징 조회 시 데이터와 nextCursor가 반환될 수 있다.")
    @Transactional
    void getContents() throws Exception {
        // given
        for (int i = 1; i <= 4; i++) {
            Content content = ContentFixture.content("T" + i, "D");
            contentRepository.save(content);
        }

        // when & then
        mockMvc.perform(get("/api/admin/contents")
                        .param("size", "2")
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andReturn();

        mockMvc.perform(get("/api/admin/contents")
                        .param("cusror", "3")
                        .param("size", "50")
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andReturn();

    }

    @Test
    @DisplayName("페이징 카테고리 필터링 조회 시 데이터와 nextCursor가 반환될 수 있다.")
    @Transactional
    void getContentsByCategory() throws Exception {
        // given
        for (int i = 1; i <= 4; i++) {
            Category category;
            if (i % 2 == 0) {
                category = categoryRepository.findByCategoryType(CategoryType.MOVIE).get();
            } else {
                category = categoryRepository.findByCategoryType(CategoryType.DRAMA).get();
            }
            Content content = ContentFixture.content("T" + i, "D");
            contentRepository.save(content);
            ContentCategoryFixture.contentCategory(content, category);
        }

        // when & then
        mockMvc.perform(get("/api/admin/contents")
                        .param("size", "3")
                        .param("categoryType", "영화")
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.item.size()").value(2))
                .andReturn();

        mockMvc.perform(get("/api/admin/contents")
                        .param("size", "3")
                        .param("categoryType", "드라마")
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.item.size()").value(2))
                .andReturn();
    }

    @Test
    @DisplayName("상세 조회 시 매핑된 필드가 반환될 수 있다.")
    @Transactional
    void getContentSuccess() throws Exception {
        //given
        Content content = ContentFixture.content("T", "D");
        contentRepository.save(content);

        // when & then
        mockMvc.perform(get("/api/admin/contents/{id}", content.getId())
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(content.getTitle()));
    }


    @DisplayName("여러 명의 출연진을 한번에 저장한다.")
    @Test
    void registerCasts() throws Exception {
        // given
        final AdminCastDTO adminCastDTO1 = new AdminCastDTO("강호동", "강호동.image.com");
        final AdminCastDTO adminCastDTO2 = new AdminCastDTO("유재석", "유재석.image.com");
        final AdminCastDTO adminCastDTO3 = new AdminCastDTO("이효리", "이효리.image.com");
        AdminCastsRegisterRequest request = new AdminCastsRegisterRequest(
                List.of(adminCastDTO1, adminCastDTO2, adminCastDTO3)
        );

        // when  // then
        mockMvc.perform(post("/api/admin/casts")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.castIds").isArray())
                .andExpect(jsonPath("$.castIds.length()").value(3))
        ;
    }

    @DisplayName("저장할 출연자 요청 값이 존재하지 않으면 다건 출연자를 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenCastRequestsIsNull() throws Exception {
        // given
        AdminCastsRegisterRequest request = new AdminCastsRegisterRequest(null);

        // when  // then
        mockMvc.perform(post("/api/admin/casts")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("출연진 정보는 필수입니다."))
        ;
    }

    @DisplayName("한 번에 31명 이상의 출연자를 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenCastCountExceedsLimit() throws Exception {
        // given
        List<AdminCastDTO> casts = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            casts.add(new AdminCastDTO("name" + i, "url" + i));
        }
        AdminCastsRegisterRequest request = new AdminCastsRegisterRequest(casts);

        // when  // then
        mockMvc.perform(post("/api/admin/casts")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("출연진은 최대 30명까지 등록할 수 있습니다."))
        ;
    }

    @DisplayName("출연진의 이름이 없다면 출연진을 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenCastNameIsEmtpy() throws Exception {
        // given
        final AdminCastDTO adminCastDTO1 = new AdminCastDTO("", "강호동.image.com");
        final AdminCastDTO adminCastDTO2 = new AdminCastDTO(null, "유재석.image.com");
        final AdminCastDTO adminCastDTO3 = new AdminCastDTO(" ", "이효리.image.com");
        AdminCastsRegisterRequest request = new AdminCastsRegisterRequest(
                List.of(adminCastDTO1, adminCastDTO2, adminCastDTO3)
        );

        // when  // then
        mockMvc.perform(post("/api/admin/casts")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("출연진 이름은 필수입니다."))
        ;
    }

    @DisplayName("출연진의 사진 URI가 없다면 출연진을 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenCastImageUriIsEmtpy() throws Exception {
        // given
        final AdminCastDTO adminCastDTO1 = new AdminCastDTO("강호동", "");
        final AdminCastDTO adminCastDTO2 = new AdminCastDTO("유재석", null);
        final AdminCastDTO adminCastDTO3 = new AdminCastDTO("이효리", " ");
        AdminCastsRegisterRequest request = new AdminCastsRegisterRequest(
                List.of(adminCastDTO1, adminCastDTO2, adminCastDTO3)
        );

        // when  // then
        mockMvc.perform(post("/api/admin/casts")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("출연진 사진 주소은 필수입니다."))
        ;
    }

    @DisplayName("이름으로 출연진을 검색한다.")
    @Test
    void getCastsByCastName() throws Exception {
        // given
        final String castName = "김두루미";
        Cast savedCast = castRepository.save(CastFixture.cast(castName));

        // when  // then
        mockMvc.perform(get("/api/admin/casts")
                        .param("name", savedCast.getCastName())
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item").isArray())
                .andExpect(jsonPath("$.item[0].castId").value(savedCast.getId()))
                .andExpect(jsonPath("$.item[0].name").value(savedCast.getCastName()))
                .andExpect(jsonPath("$.nextCursor").isEmpty())
                .andExpect(jsonPath("$.hasNext").value(Boolean.FALSE))
        ;
    }

    @DisplayName("이름 없이 요청 시 출연진을 검색할 수 없다.")
    @Test
    void throwValidExceptionWhenCastNameIsBlank() throws Exception {
        // when  // then
        mockMvc.perform(get("/api/admin/casts")
                        .param("name", "")
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("검색할 출연진 이름은 필수입니다."))
        ;
    }

    @DisplayName("이름을 이용한 출연진 검색 인원은 최소 1명이야 한다.")
    @Test
    void throwValidExceptionWhenCastSizeIsLessThanOne() throws Exception {
        // given
        final String size = "0";
        final String castName = "김두루미";
        Cast savedCast = castRepository.save(CastFixture.cast(castName));

        // when  // then
        mockMvc.perform(get("/api/admin/casts")
                        .param("name", savedCast.getCastName())
                        .param("size", size)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("출연진은 최소 1명 이상 조회해야 합니다."))
        ;
    }

    @DisplayName("이름을 이용한 출연진 검색 인원은 최대 20명을 넘을 수 없다.")
    @Test
    void throwValidExceptionWhenCastSizeExceedsLimit() throws Exception {
        // given
        final String size = "21";
        final String castName = "김두루미";
        Cast savedCast = castRepository.save(CastFixture.cast(castName));

        // when  // then
        mockMvc.perform(get("/api/admin/casts")
                        .param("name", savedCast.getCastName())
                        .param("size", size)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("출연진은 최대 20명 조회할 수 있습니다."))
        ;
    }

    @DisplayName("여러 명의 감독을 한번에 저장한다.")
    @Test
    void registerDirectors() throws Exception {
        // given
        final AdminDirectorDTO adminDirectorDTO1 = new AdminDirectorDTO("봉준호", "봉준호.image.com");
        final AdminDirectorDTO adminDirectorDTO2 = new AdminDirectorDTO("박찬욱", "박찬욱.image.com");
        final AdminDirectorDTO adminDirectorDTO3 = new AdminDirectorDTO("류승완", "류승완.image.com");
        AdminDirectorsRegisterRequest request = new AdminDirectorsRegisterRequest(
                List.of(adminDirectorDTO1, adminDirectorDTO2, adminDirectorDTO3)
        );

        // when  // then
        mockMvc.perform(post("/api/admin/directors")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.directorIds").isArray())
                .andExpect(jsonPath("$.directorIds.length()").value(3))
        ;
    }

    @DisplayName("저장할 감독 요청 값이 존재하지 않으면 다건 감독을 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenDirectorRequestsIsNull() throws Exception {
        // given
        AdminDirectorsRegisterRequest request = new AdminDirectorsRegisterRequest(null);

        // when  // then
        mockMvc.perform(post("/api/admin/directors")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("감독 정보는 필수입니다."))
        ;
    }

    @DisplayName("한 번에 31명 이상의 감독를 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenDirectorCountExceedsLimit() throws Exception {
        // given
        List<AdminDirectorDTO> directors = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            directors.add(new AdminDirectorDTO("name" + i, "url" + i));
        }
        AdminDirectorsRegisterRequest request = new AdminDirectorsRegisterRequest(directors);

        // when  // then
        mockMvc.perform(post("/api/admin/directors")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("감독은 최대 30명까지 등록할 수 있습니다."))
        ;
    }

    @DisplayName("감독의 이름이 없다면 감독을 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenDirectorNameIsEmtpy() throws Exception {
        // given
        final AdminDirectorDTO adminDirectorDTO1 = new AdminDirectorDTO("", "봉준호.image.com");
        final AdminDirectorDTO adminDirectorDTO2 = new AdminDirectorDTO(null, "박찬욱.image.com");
        final AdminDirectorDTO adminDirectorDTO3 = new AdminDirectorDTO(" ", "류승완.image.com");
        AdminDirectorsRegisterRequest request = new AdminDirectorsRegisterRequest(
                List.of(adminDirectorDTO1, adminDirectorDTO2, adminDirectorDTO3)
        );

        // when  // then
        mockMvc.perform(post("/api/admin/directors")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("감독 이름은 필수입니다."))
        ;
    }

    @DisplayName("감독의 사진 URI가 없다면 감독을 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenDirectorImageUriIsEmtpy() throws Exception {
        // given
        final AdminDirectorDTO adminDirectorDTO1 = new AdminDirectorDTO("봉준호", "");
        final AdminDirectorDTO adminDirectorDTO2 = new AdminDirectorDTO("박찬욱", null);
        final AdminDirectorDTO adminDirectorDTO3 = new AdminDirectorDTO("류승완", " ");
        AdminDirectorsRegisterRequest request = new AdminDirectorsRegisterRequest(
                List.of(adminDirectorDTO1, adminDirectorDTO2, adminDirectorDTO3)
        );

        // when  // then
        mockMvc.perform(post("/api/admin/directors")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("감독 사진 주소은 필수입니다."))
        ;
    }


    @DisplayName("이름으로 감독을 검색한다.")
    @Test
    void getDirectorsByDirectorName() throws Exception {
        // given
        final String directorName = "김두루미";
        Director savedDirector = directorRepository.save(DirectorFixture.director(directorName));

        // when  // then
        mockMvc.perform(get("/api/admin/directors")
                        .param("name", savedDirector.getDirectorName())
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item").isArray())
                .andExpect(jsonPath("$.item[0].directorId").value(savedDirector.getId()))
                .andExpect(jsonPath("$.item[0].name").value(savedDirector.getDirectorName()))
                .andExpect(jsonPath("$.nextCursor").isEmpty())
                .andExpect(jsonPath("$.hasNext").value(Boolean.FALSE))
        ;
    }

    @DisplayName("이름 없이 요청 시 감독을 검색할 수 없다.")
    @Test
    void throwValidExceptionWhenDirectorNameIsBlank() throws Exception {
        // when  // then
        mockMvc.perform(get("/api/admin/directors")
                        .param("name", "")
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("검색할 감독 이름은 필수입니다."))
        ;
    }

    @DisplayName("이름을 이용한 감독 검색 인원은 최소 1명이야 한다.")
    @Test
    void throwValidExceptionWhenDirectorSizeIsLessThanOne() throws Exception {
        // given
        final String size = "0";
        final String directorName = "봉준호";

        // when  // then
        mockMvc.perform(get("/api/admin/directors")
                        .param("name", directorName)
                        .param("size", size)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("감독은 최소 1명 이상 조회해야 합니다."))
        ;
    }

    @DisplayName("이름을 이용한 감독 검색 인원은 최대 20명을 넘을 수 없다.")
    @Test
    void throwValidExceptionWhenDirectorSizeExceedsLimit() throws Exception {
        // given
        final String size = "21";
        final String directorName = "봉준호";

        // when  // then
        mockMvc.perform(get("/api/admin/directors")
                        .param("name", directorName)
                        .param("size", size)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("감독은 최대 20명 조회할 수 있습니다."))
        ;
    }

    @DisplayName("배치 수정 작업의 상세 정보를 조회할 수 있다.")
    @Test
    void getBatchUpdateJobDetail() throws Exception {
        // given
        adminContentDeleteJobRepository.save(
                AdminContentDeleteJobFixture.createPendingJob(1L, 1L));

        Long jobId = 1L;

        // when & then
        mockMvc.perform(get("/api/admin/batch/contents/deletejob/{jobId}", jobId)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk());
    }
}
