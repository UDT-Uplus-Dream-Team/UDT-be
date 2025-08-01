package com.example.udtbe.admin.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.fixture.CastFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.admin.dto.common.AdminCastDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.service.AdminQuery;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Content;
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
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

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
    private AdminQuery adminQuery;

    private AdminContentRegisterRequest adminContentRegisterRequest;

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

    @BeforeEach
    void setup() throws Exception {
        adminContentRegisterRequest = new AdminContentRegisterRequest(
                "테스트 제목", "테스트 설명",
                "https://poster", "https://backdrop", "https://trailer",
                LocalDateTime.of(2025, 7, 11, 0, 0),
                100, 1, "전체관람가",
                List.of(new AdminCategoryDTO("영화", List.of("액션"))),
                List.of("한국"), List.of(1L, 2L),
                List.of(1L, 2L),
                List.of(new AdminPlatformDTO("넷플릭스", "https://watch"))
        );
    }

    @Test
    @DisplayName("콘텐츠 등록 시 DB에 저장할 수 있다.")
    void contentRegister() throws Exception {
        // when & then
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(adminContentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @DisplayName("콘텐츠 수정 시 필드와 메타데이터가 변경될 수 있다.")
    void updateContent() throws Exception {
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
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(adminContentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(mr.getResponse().getContentAsString());
        Long id = node.get("contentId").asLong();

        // when & then
        mockMvc.perform(patch("/api/admin/contents/{id}", id)
                        .content(objectMapper.writeValueAsString(adminContentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("페이징 조회 시 데이터와 nextCursor가 반환될 수 있다.")
    void getContents() throws Exception {
        // given
        for (int i = 1; i <= 4; i++) {
            new AdminContentRegisterRequest(
                    "T" + i, "D",
                    "p", "b", "t",
                    LocalDateTime.now(), 10, 1, "전체",
                    List.of(new AdminCategoryDTO("영화", List.of("액션"))),
                    List.of("KR"), List.of((long) i),
                    List.of((long) i),
                    List.of(new AdminPlatformDTO("넷플릭스", "u"))
            );

            mockMvc.perform(post("/api/admin/contents")
                            .content(objectMapper.writeValueAsString(adminContentRegisterRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(accessTokenOfAdmin)
                    )
                    .andExpect(status().isCreated())
                    .andReturn();
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
    void getContentsByCategory() throws Exception {
        // given
        for (int i = 1; i <= 4; i++) {
            String categoryType;
            if (i % 2 == 0) {
                categoryType = "영화";
            } else {
                categoryType = "드라마";
            }
            AdminContentRegisterRequest adminContentRegisterRequest1 = new AdminContentRegisterRequest(
                    "T" + i, "D",
                    "p", "b", "t",
                    LocalDateTime.now(), 10, 1, "전체",
                    List.of(
                            new AdminCategoryDTO(categoryType, List.of("서사/드라마")),
                            new AdminCategoryDTO("애니메이션", List.of("키즈"))
                    ),
                    List.of("KR"), List.of((long) i),
                    List.of((long) i),
                    List.of(new AdminPlatformDTO("넷플릭스", "u"))
            );

            mockMvc.perform(post("/api/admin/contents")
                            .content(objectMapper.writeValueAsString(adminContentRegisterRequest1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(accessTokenOfAdmin)
                    )
                    .andExpect(status().isCreated())
                    .andReturn();
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
    void getContentSuccess() throws Exception {
        //given
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(adminContentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(mr.getResponse().getContentAsString());
        Long id = node.get("contentId").asLong();

        // when & then
        mockMvc.perform(get("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(adminContentRegisterRequest.title()))
                .andExpect(jsonPath("$.categories[0].categoryType")
                        .value(adminContentRegisterRequest.categories().get(0).categoryType()));
    }


    @Test
    @DisplayName("삭제 시 isDeleted 플래그가 설정될 수 있다. 그 다음 조회 시 404에러가 나올 수 있다.")
    void deleteContent() throws Exception {
        // given
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(adminContentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(mr.getResponse().getContentAsString());
        Long id = node.get("contentId").asLong();

        // when
        mockMvc.perform(delete("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isNoContent());

        Content c = contentRepository.findById(id).orElseThrow();
        assertThat(c.isDeleted()).isTrue();

        // 조회 시 404
        mockMvc.perform(get("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfAdmin)
                )
                .andExpect(status().isNotFound());
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

    @DisplayName("요청 값이 존재하지 않으면 다건 출연자를 저장할 수 없다.")
    @Test
    void throwValidExceptionWhenRequestIsNull() throws Exception {
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

}
