package com.example.udtbe.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.ContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.ContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.ContentRegisterResponse;
import com.example.udtbe.domain.admin.service.AdminQuery;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock private ContentRepository contentRepository;
    @Mock private ContentMetadataRepository contentMetadataRepository;
    @Mock private AdminQuery adminQuery;

    @InjectMocks private AdminService adminService;

    @Captor private ArgumentCaptor<Content> contentCaptor;
    @Captor private ArgumentCaptor<ContentMetadata> metadataCaptor;

    private ContentRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new ContentRegisterRequest(
                "테스트제목",
                "테스트설명",
                "https://poster",
                "https://backdrop",
                "https://trailer",
                LocalDateTime.of(2025,7,11,0,0),
                120,
                0,
                "전체관람가",
                List.of(new CategoryDTO("영화", List.of("액션"))),
                List.of("KOREA"),
                List.of("감독님"),
                List.of(new CastDTO("배우님","https://cast.image")),
                List.of(new PlatformDTO("넷플릭스","https://watch",true))
        );
    }

    @DisplayName("콘텐츠와 메타데이터를 저장한다.")
    @Test
    void contentRegister() {
        // given
        Content saved = mock(Content.class);
        given(saved.getId()).willReturn(42L);
        given(contentRepository.save(any(Content.class))).willReturn(saved);

        CategoryDTO catDto = registerRequest.categories().get(0);
        given(adminQuery.findByCategoryType(
                CategoryType.fromByType(catDto.categoryType())))
                .willReturn(mock(Category.class));

        CastDTO castDto = registerRequest.casts().get(0);
        given(adminQuery.findOrSaveCast(
                castDto.castName(), castDto.castImageUrl()))
                .willReturn(mock(Cast.class));

        given(adminQuery.findOrSaveDirector(
                registerRequest.directors().get(0)))
                .willReturn(mock(Director.class));

        given(adminQuery.findOrSaveCountry(
                registerRequest.countries().get(0)))
                .willReturn(mock(Country.class));

        PlatformDTO platDto = registerRequest.platforms().get(0);
        given(adminQuery.findByPlatform(
                PlatformType.fromByType(platDto.platformType())))
                .willReturn(mock(Platform.class));

        // when
        ContentRegisterResponse resp = adminService.contentRegister(registerRequest);

        // then
        assertThat(resp.contentId()).isEqualTo(42L);
        then(contentRepository).should().save(contentCaptor.capture());
        Content captured = contentCaptor.getValue();
        assertThat(captured.getTitle()).isEqualTo(registerRequest.title());
        assertThat(captured.getDescription()).isEqualTo(registerRequest.description());

        then(adminQuery).should(times(2))
                .findByCategoryType(
                        CategoryType.fromByType(catDto.categoryType())
                );
        then(adminQuery).should().findOrSaveCast(
                castDto.castName(), castDto.castImageUrl()
        );
        then(adminQuery).should().findOrSaveDirector(
                registerRequest.directors().get(0)
        );
        then(adminQuery).should().findOrSaveCountry(
                registerRequest.countries().get(0)
        );
        then(adminQuery).should().findByPlatform(
                PlatformType.fromByType(platDto.platformType())
        );

        then(contentMetadataRepository)
                .should()
                .save(metadataCaptor.capture());
    }

    @DisplayName("저장 요청 분류 타입이 ENUM에 정의되어 있는 분류 타입과 맞지 않으면 404 에러가 발생한다.")
    @Test
    void saveInvalidCategoryType() {
        // given
        CategoryDTO bad = new CategoryDTO("UNKNOWN", List.of());
        ContentRegisterRequest badReq = new ContentRegisterRequest(
                registerRequest.title(),
                registerRequest.description(),
                registerRequest.posterUrl(),
                registerRequest.backdropUrl(),
                registerRequest.trailerUrl(),
                registerRequest.openDate(),
                registerRequest.runningTime(),
                registerRequest.episode(),
                registerRequest.rating(),
                List.of(bad),
                registerRequest.countries(),
                registerRequest.directors(),
                registerRequest.casts(),
                registerRequest.platforms()
        );

        // expect
        RestApiException ex = assertThrows(
                RestApiException.class,
                () -> adminService.contentRegister(badReq)
        );
        assertThat(ex.getErrorCode())
                .isEqualTo(EnumErrorCode.CATEGORY_TYPE_NOT_FOUND);
    }

    @DisplayName("관리자는 콘텐츠를 업데이트할 때 필드와 메타데이터를 수정한다")
    @Test
    void updateContentSuccess() {
        // given
        Long id = 42L;
        Content content = mock(Content.class);
        ContentMetadata metadata = mock(ContentMetadata.class);
        given(adminQuery.findContentByContentId(id)).willReturn(content);
        given(adminQuery.findContentMetadateByContentId(id))
                .willReturn(metadata);

        ContentUpdateRequest upd = new ContentUpdateRequest(
                "수정제목","수정설명",
                "https://new-poster","https://new-backdrop","https://new-trailer",
                LocalDateTime.of(2025,7,12,0,0),
                130,1,"12세관람가",
                List.of(new CategoryDTO("애니메이션",List.of("키즈"))),
                List.of("USA"),
                List.of("NewDirector"),
                List.of(new CastDTO("NewCast","https://new-image")),
                List.of(new PlatformDTO("디즈니+","https://watch",false))
        );

        CategoryDTO ct = upd.categories().get(0);
        Category cat = mock(Category.class);
        given(adminQuery.findByCategoryType(
                CategoryType.fromByType(ct.categoryType())))
                .willReturn(cat);
        given(cat.getGenres()).willReturn(List.of());

        CastDTO cDto = upd.casts().get(0);
        given(adminQuery.findOrSaveCast(
                cDto.castName(), cDto.castImageUrl()))
                .willReturn(mock(Cast.class));
        given(adminQuery.findOrSaveDirector(upd.directors().get(0)))
                .willReturn(mock(Director.class));
        given(adminQuery.findOrSaveCountry(upd.countries().get(0)))
                .willReturn(mock(Country.class));
        PlatformDTO pDto = upd.platforms().get(0);
        given(adminQuery.findByPlatform(
                PlatformType.fromByType(pDto.platformType())))
                .willReturn(mock(Platform.class));

        // when
        adminService.updateContent(id, upd);

        // then: 필드업데이트 + 관계초기화
        then(content).should().update(
                upd.title(), upd.description(), upd.posterUrl(), upd.backdropUrl(),
                upd.trailerUrl(), upd.openDate(), upd.runningTime(), upd.episode(), upd.rating()
        );
        then(content).should().clearAllRelations();

        // 관계 로직
        then(adminQuery).should(times(2))
                .findByCategoryType(CategoryType.fromByType(ct.categoryType()));
        then(adminQuery).should().findOrSaveCast(cDto.castName(), cDto.castImageUrl());
        then(adminQuery).should().findOrSaveDirector(upd.directors().get(0));
        then(adminQuery).should().findOrSaveCountry(upd.countries().get(0));
        then(adminQuery).should().findByPlatform(
                PlatformType.fromByType(pDto.platformType()));

        // 메타데이터
        List<String> genres = upd.categories().stream()
                .flatMap(c -> c.genres().stream()).distinct().toList();
        List<String> platforms = upd.platforms().stream()
                .map(PlatformDTO::platformType).toList();
        then(metadata).should().update(
                upd.title(), upd.rating(), platforms, upd.directors(), genres
        );
    }

    @DisplayName("커서 기반 페이지네이션 결과를 반환한다")
    @Test
    void getContents() {
        // given
        ContentDTO d1 = new ContentDTO(5L, "T5","p5",LocalDateTime.now(),"전체관람가");
        ContentDTO d2 = new ContentDTO(4L, "T4","p4",LocalDateTime.now(),"15세");
        CursorPageResponse<ContentDTO> page =
                new CursorPageResponse<>(List.of(d1,d2), "4", true);
        given(contentRepository.findContentsAdminByCursor(5L,2))
                .willReturn(page);

        // when
        CursorPageResponse<ContentDTO> res = adminService.getContents(5L,2);

        // then
        assertThat(res).isSameAs(page);
        then(contentRepository).should()
                .findContentsAdminByCursor(5L,2);
    }

    @DisplayName("getContent: 정상 조회 시 필드와 연관관계가 매핑되어 반환된다")
    @Test
    void getContentSuccess() {
        // given
        Long id = 100L;
        Content full = spy(ContentFixture.content(
                "타이틀","설명","영화","액션","넷플릭스"
        ));
        given(adminQuery.findContentByContentId(id)).willReturn(full);

        // when
        ContentGetDetailResponse resp = adminService.getContent(id);

        // then
        ContentGetDetailResponse expected = new ContentGetDetailResponse(
                full.getTitle(), full.getDescription(),
                full.getPosterUrl(), full.getBackdropUrl(), full.getTrailerUrl(),
                full.getOpenDate(), full.getRunningTime(), full.getEpisode(), full.getRating(),
                List.of(new CategoryDTO("영화", List.of("액션"))),
                List.of("한국"),
                List.of("감독A"),
                List.of(new CastDTO("배우A","https://example.com/castA")),
                List.of(new PlatformDTO("넷플릭스","https://example.com/watch", true))
        );
        assertThat(resp).usingRecursiveComparison().isEqualTo(expected);
        then(adminQuery).should().findContentByContentId(id);
    }

    @DisplayName("deleteContent: 콘텐츠를 삭제할 때 isDeleted 플래그를 설정한다")
    @Test
    void deleteContentSuccess() {
        // given
        Long id = 300L;
        Content content = mock(Content.class);
        ContentMetadata metadata = mock(ContentMetadata.class);
        given(adminQuery.findContentByContentId(id)).willReturn(content);
        given(adminQuery.findContentMetadateByContentId(id))
                .willReturn(metadata);

        // when
        adminService.deleteContent(id);

        // then
        then(content).should().delete(true);
        then(adminQuery).should().findContentByContentId(id);
    }

    @DisplayName("삭제된 콘텐츠 조회 시 404 예외가 발생한다")
    @Test
    void getContentDeletedNotFound() {
        // given
        Long id = 200L;
        Content deleted = spy(ContentFixture.content(
                "타이틀","설명","영화","액션","넷플릭스"
        ));
        given(adminQuery.findContentByContentId(id)).willReturn(deleted);
        given(deleted.isDeleted()).willReturn(true);

        // when / then
        RestApiException ex = assertThrows(
                RestApiException.class,
                () -> adminService.getContent(id)
        );
        assertThat(ex.getErrorCode())
                .isEqualTo(ContentErrorCode.CONTENT_NOT_FOUND);
    }
}
