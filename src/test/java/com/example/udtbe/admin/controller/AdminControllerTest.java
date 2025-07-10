package com.example.udtbe.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.example.udtbe.domain.admin.dto.response.ContentRegisterResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

@Sql(scripts = "classpath:data-test.sql")
public class AdminControllerTest extends ApiSupport {

    @Autowired
    private ObjectMapper objectMapper;

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

    // helper
    Long createContentAndGetId(ContentRegisterRequest req) throws Exception {
        MvcResult mr = mockMvc.perform(post("/api/admin/contents")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String body = mr.getResponse().getContentAsString();
        return objectMapper.readValue(body, ContentRegisterResponse.class).contentId();
    }

    @Test
    @DisplayName("콘텐츠 등록 → DB에 저장된다")
    void registerContent() throws Exception {
        ContentRegisterRequest req = new ContentRegisterRequest(
                "제목", "설명",
                "poster", "backdrop", "trailer",
                LocalDateTime.of(2025,7,11,0,0),
                100, 1, "전체관람가",
                List.of(new CategoryDTO("영화", List.of("액션"))),
                List.of("한국"), List.of("감독A"),
                List.of(new CastDTO("배우A","cast.jpg")),
                List.of(new PlatformDTO("넷플릭스","watch",true))
        );

        Long id = createContentAndGetId(req);

        Content saved = contentRepository.findById(id).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("제목");
        assertThat(contentMetadataRepository.findByContent_Id(id).get().getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("콘텐츠 수정 → 필드와 메타데이터가 변경된다")
    void updateContent() throws Exception {
        // given
        ContentRegisterRequest regReq = new ContentRegisterRequest(
                "타이틀1", "설명1",
                "p1","b1","t1",
                LocalDateTime.of(2025,7,11,0,0),
                90,1,"12세",
                List.of(new CategoryDTO("영화",List.of("액션"))),
                List.of("한국"),List.of("감독A"),
                List.of(new CastDTO("배우A","c1")),
                List.of(new PlatformDTO("넷플릭스","w1",true))
        );
        Long id = createContentAndGetId(regReq);

        ContentUpdateRequest upd = new ContentUpdateRequest(
                "타이틀2","설명2","p2","b2","t2",
                LocalDateTime.of(2025,7,12,0,0),
                120,2,"15세",
                List.of(new CategoryDTO("애니메이션",List.of("키즈"))),
                List.of("미국"),List.of("감독B"),
                List.of(new CastDTO("배우B","c2")),
                List.of(new PlatformDTO("디즈니+","w2",false))
        );

        // when
        mockMvc.perform(patch("/api/admin/contents/{id}", id)
                        .content(objectMapper.writeValueAsString(upd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNoContent());

        // then
        Content after = contentRepository.findById(id).orElseThrow();
        assertThat(after.getTitle()).isEqualTo("타이틀2");
        ContentMetadata meta = contentMetadataRepository
                .findByContent_Id(id).orElseThrow();
        assertThat(meta.getTitle()).isEqualTo("타이틀2");
    }

    @Test
    @DisplayName("페이징 조회 → 데이터와 nextCursor가 반환된다")
    void getContents() throws Exception {
        // 여러 콘텐츠 생성
        for(int i=1; i<=4; i++) {
            ContentRegisterRequest r = new ContentRegisterRequest(
                    "T"+i, "D",
                    "p","b","t",
                    LocalDateTime.now(),10,1,"전체",
                    List.of(new CategoryDTO("영화",List.of("액션"))),
                    List.of("KR"),List.of("D"),
                    List.of(new CastDTO("C","u")),
                    List.of(new PlatformDTO("넷플릭스","u",true))
            );
            createContentAndGetId(r);
        }

        mockMvc.perform(get("/api/admin/contents")
                        //.param("cusror","")
                        .param("size","2")
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andReturn();

        mockMvc.perform(get("/api/admin/contents")
                        .param("cusror","3")
                        .param("size","50")
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andReturn();

    }

    @Test
    @DisplayName("상세 조회 → 매핑된 필드가 반환된다")
    void getContentSuccess_integration() throws Exception {
        ContentRegisterRequest req = new ContentRegisterRequest(
                "T", "D",
                "p","b","t",
                LocalDateTime.of(2025,7,1,0,0),
                50,1,"12세",
                List.of(new CategoryDTO("영화",List.of("액션"))),
                List.of("KR"),List.of("D"),
                List.of(new CastDTO("C","u")),
                List.of(new PlatformDTO("넷플릭스","u",true))
        );
        Long id = createContentAndGetId(req);

        mockMvc.perform(get("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("T"))
                .andExpect(jsonPath("$.categories[0].categoryType").value("영화"));
    }

    @Test
    @DisplayName("삭제 → isDeleted 플래그가 설정되고, 이후 조회 404")
    void deleteContent() throws Exception {
        ContentRegisterRequest req = new ContentRegisterRequest(
                "T3","D3","p","b","t",
                LocalDateTime.now(),20,1,"전체",
                List.of(new CategoryDTO("영화",List.of("액션"))),
                List.of("KR"),List.of("D"),
                List.of(new CastDTO("C","u")),
                List.of(new PlatformDTO("넷플릭스","u",true))
        );
        Long id = createContentAndGetId(req);

        mockMvc.perform(delete("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNoContent());

        // Soft delete flag
        Content c = contentRepository.findById(id).orElseThrow();
        assertThat(c.isDeleted()).isTrue();

        // 조회 시 404
        mockMvc.perform(get("/api/admin/contents/{id}", id)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNotFound());
    }
}
