package com.example.udtbe.admin.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.ContentUpdateRequest;
import com.example.udtbe.domain.admin.service.AdminQuery;
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

    private ContentRegisterRequest contentRegisterRequest;

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
        contentRegisterRequest = new ContentRegisterRequest(
                "테스트 제목", "테스트 설명",
                "https://poster", "https://backdrop", "https://trailer",
                LocalDateTime.of(2025, 7, 11, 0, 0),
                100, 1, "전체관람가",
                List.of(new CategoryDTO("영화", List.of("액션"))),
                List.of("한국"), List.of("테스트 감독"),
                List.of(new CastDTO("테스트 배우", "https://cast.jpg")),
                List.of(new PlatformDTO("넷플릭스", "https://watch", true))
        );
    }

    @Test
    @DisplayName("콘텐츠 등록 → DB에 저장된다")
    void contentRegister() throws Exception {
        // when & then
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(contentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @DisplayName("콘텐츠 수정 → 필드와 메타데이터가 변경된다")
    void updateContent() throws Exception {
        // given
        ContentUpdateRequest contentUpdateRequest = new ContentUpdateRequest(
                "수정 테스트 제목", "수정 테스트 설명",
                "p1", "b1", "t1",
                LocalDateTime.of(2025, 7, 11, 0, 0),
                90, 1, "12세",
                List.of(
                        new CategoryDTO("애니메이션", List.of("키즈")),
                        new CategoryDTO("드라마", List.of("서사/드라마"))
                ),
                List.of("한국"), List.of("수정 테스트 감독"),
                List.of(new CastDTO("수정 테스트 배우", "c1")),
                List.of(
                        new PlatformDTO("넷플릭스", "w1", true),
                        new PlatformDTO("디즈니+", "w2", false)
                )
        );
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(contentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(mr.getResponse().getContentAsString());
        Long id = node.get("contentId").asLong();

        // when & then
        mockMvc.perform(patch("/api/admin/contents/{id}", id)
                        .content(objectMapper.writeValueAsString(contentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("페이징 조회 → 데이터와 nextCursor가 반환된다")
    void getContents() throws Exception {
        // given
        for (int i = 1; i <= 4; i++) {
            ContentRegisterRequest contentRegisterRequest1 = new ContentRegisterRequest(
                    "T" + i, "D",
                    "p", "b", "t",
                    LocalDateTime.now(), 10, 1, "전체",
                    List.of(new CategoryDTO("영화", List.of("액션"))),
                    List.of("KR"), List.of("D"),
                    List.of(new CastDTO("C", "u")),
                    List.of(new PlatformDTO("넷플릭스", "u", true))
            );
            mockMvc.perform(post("/api/admin/contents")
                            .content(objectMapper.writeValueAsString(contentRegisterRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(accessTokenOfTempMember)
                    )
                    .andExpect(status().isCreated())
                    .andReturn();
        }

        // when & then
        mockMvc.perform(get("/api/admin/contents")
                        .param("size", "2")
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andReturn();

        mockMvc.perform(get("/api/admin/contents")
                        .param("cusror", "3")
                        .param("size", "50")
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andReturn();

    }

    @Test
    @DisplayName("상세 조회 → 매핑된 필드가 반환된다")
    void getContentSuccess() throws Exception {
        //given
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(contentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(mr.getResponse().getContentAsString());
        Long id = node.get("contentId").asLong();

        // when & then
        mockMvc.perform(get("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(contentRegisterRequest.title()))
                .andExpect(jsonPath("$.categories[0].categoryType")
                        .value(contentRegisterRequest.categories().get(0).categoryType()));
    }


    @Test
    @DisplayName("삭제 → isDeleted 플래그가 설정, 그 다음 조회 시 404")
    void deleteContent() throws Exception {
        // given
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(contentRegisterRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(mr.getResponse().getContentAsString());
        Long id = node.get("contentId").asLong();

        // when
        mockMvc.perform(delete("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNoContent());

        Content c = contentRepository.findById(id).orElseThrow();
        assertThat(c.isDeleted()).isTrue();

        // 조회 시 404
        mockMvc.perform(get("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNotFound());
    }
}
