package com.example.udtbe.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.domain.admin.dto.common.AdminCastDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.service.AdminQuery;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.repository.ContentCastRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCountryRepository;
import com.example.udtbe.domain.content.repository.ContentDirectorRepository;
import com.example.udtbe.domain.content.repository.ContentGenreRepository;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentPlatformRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private ContentPlatformRepository contentPlatformRepository;
    @Mock
    private ContentCastRepository contentCastRepository;
    @Mock
    private ContentDirectorRepository contentDirectorRepository;
    @Mock
    private ContentCountryRepository contentCountryRepository;
    @Mock
    private ContentCategoryRepository contentCategoryRepository;
    @Mock
    private ContentGenreRepository contentGenreRepository;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private ContentMetadataRepository contentMetadataRepository;
    @Mock
    private AdminQuery adminQuery;

    @InjectMocks
    private AdminService adminService;

    private AdminContentRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new AdminContentRegisterRequest(
                "테스트 제목",
                "테스트 설명",
                "https://poster",
                "https://backdrop",
                "https://trailer",
                LocalDateTime.of(2025, 7, 11, 0, 0),
                120,
                0,
                "전체 관람가",
                List.of(
                        new AdminCategoryDTO("영화", List.of("액션", "SF")),
                        new AdminCategoryDTO("애니메이션", List.of("키즈"))
                ),
                List.of("대한민국"),
                List.of("테스트 감독"),
                List.of(
                        new AdminCastDTO("테스트 배우", "https://cast.image1"),
                        new AdminCastDTO("테스트 배우2", "https://cast.image2")
                ),
                List.of(
                        new AdminPlatformDTO("넷플릭스", "https://watch1"),
                        new AdminPlatformDTO("왓챠", "https://watch2")
                )
        );
    }

    @DisplayName("콘텐츠와 메타데이터를 저장할 수 있다.")
    @Test
    void contentRegister() {
        // given
        Long id = 42L;
        Content saved = mock(Content.class);
        given(saved.getId()).willReturn(id);
        given(contentRepository.save(any(Content.class))).willReturn(saved);

        List<AdminCategoryDTO> adminCategoryDTOS = registerRequest.categories();

        for (AdminCategoryDTO adminCategoryDTO : adminCategoryDTOS) {
            Category category = mock(Category.class);
            given(adminQuery.findByCategoryType(
                    CategoryType.fromByType(adminCategoryDTO.categoryType())))
                    .willReturn(category);
            for (String genreName : adminCategoryDTO.genres()) {
                GenreType genreType = GenreType.fromByType(genreName);
                given(adminQuery.findByGenreTypeAndCategory(genreType, category))
                        .willReturn(mock(Genre.class));
            }
        }

        List<AdminCastDTO> adminCastDtos = registerRequest.casts();
        for (AdminCastDTO adminCastDto : adminCastDtos) {
            given(adminQuery.findOrSaveCast(eq(adminCastDto.castName()),
                    eq(adminCastDto.castImageUrl())))
                    .willReturn(mock(Cast.class));
        }

        for (String directorName : registerRequest.directors()) {
            given(adminQuery.findOrSaveDirector(
                    eq(directorName)))
                    .willReturn(mock(Director.class));
        }

        for (String countryName : registerRequest.countries()) {
            given(adminQuery.findOrSaveCountry(
                    eq(countryName)))
                    .willReturn(mock(Country.class));
        }

        List<AdminPlatformDTO> adminPlatformDTOS = registerRequest.platforms();
        for (AdminPlatformDTO platDto : adminPlatformDTOS) {
            PlatformType platformType = PlatformType.fromByType(platDto.platformType());
            given(adminQuery.findByPlatform(
                    eq(platformType)))
                    .willReturn(mock(Platform.class));
        }

        given(contentMetadataRepository.save(
                any(ContentMetadata.class)))
                .willAnswer(inv -> inv.getArgument(0));

        // when
        adminService.registerContent(registerRequest);

        int genresSize = adminCategoryDTOS.stream()
                .mapToInt(dto -> dto.genres().size()).sum();

        // then
        assertAll(
                () -> verify(contentRepository).save(any(Content.class)),

                () -> verify(adminQuery, times(registerRequest.categories().size() * 2))
                        .findByCategoryType(any(CategoryType.class)),

                () -> verify(adminQuery, times(genresSize))
                        .findByGenreTypeAndCategory(any(GenreType.class), any(Category.class)),

                () -> verify(adminQuery, times(adminCastDtos.size()))
                        .findOrSaveCast(anyString(), anyString()),

                () -> verify(adminQuery, times(registerRequest.directors().size()))
                        .findOrSaveDirector(anyString()),

                () -> verify(adminQuery, times(registerRequest.countries().size()))
                        .findOrSaveCountry(anyString()),

                () -> verify(adminQuery, times(registerRequest.platforms().size()))
                        .findByPlatform(any(PlatformType.class)),

                () -> verify(contentMetadataRepository).save(any(ContentMetadata.class))

        );
    }

    @DisplayName("저장 요청 분류 타입이 ENUM에 정의되어 있는 분류 타입과 맞지 않으면 404 에러가 발생할 수 있다.")
    @Test
    void saveInvalidCategoryType() {
        // given
        AdminCategoryDTO badAdminCategoryDTO = new AdminCategoryDTO("UNKNOWN", List.of());
        AdminContentRegisterRequest badReq = new AdminContentRegisterRequest(
                registerRequest.title(),
                registerRequest.description(),
                registerRequest.posterUrl(),
                registerRequest.backdropUrl(),
                registerRequest.trailerUrl(),
                registerRequest.openDate(),
                registerRequest.runningTime(),
                registerRequest.episode(),
                registerRequest.rating(),
                List.of(badAdminCategoryDTO),
                registerRequest.countries(),
                registerRequest.directors(),
                registerRequest.casts(),
                registerRequest.platforms()
        );

        assertThatThrownBy(() -> adminService.registerContent(badReq))
                .isInstanceOf(RestApiException.class)
                .hasMessage(EnumErrorCode.CATEGORY_TYPE_BAD_REQUEST.getMessage());

    }

    @DisplayName("관리자는 콘텐츠를 업데이트할 때 필드와 메타데이터를 수정할 수 있다.")
    @Test
    void updateContent() {
        // given
        Long id = 42L;
        Content content = mock(Content.class);
        ContentMetadata metadata = mock(ContentMetadata.class);

        given(adminQuery.findContentByContentId(id)).willReturn(content);
        given(adminQuery.findContentMetadateByContentId(id))
                .willReturn(metadata);

        AdminContentUpdateRequest adminContentUpdateRequest = new AdminContentUpdateRequest(
                "수정 테스트 제목", "수정 테스트 설명",
                "https://new-poster", "https://new-backdrop", "https://new-trailer",
                LocalDateTime.of(2025, 7, 10, 0, 0),
                130, 1, "19세 관람가",
                List.of(new AdminCategoryDTO("애니메이션", List.of("키즈"))),
                List.of("미국"),
                List.of("수정 테스트 감독"),
                List.of(new AdminCastDTO("수정 테스트 배우", "https://new-image")),
                List.of(new AdminPlatformDTO("디즈니+", "https://watch"))
        );

        List<AdminCategoryDTO> adminCategoryDTO = adminContentUpdateRequest.categories();
        for (AdminCategoryDTO dto : adminCategoryDTO) {
            Category category = mock(Category.class);
            given(adminQuery.findByCategoryType(
                    CategoryType.fromByType(dto.categoryType())))
                    .willReturn(category);
            for (String genreName : dto.genres()) {
                GenreType genreType = GenreType.fromByType(genreName);
                given(adminQuery.findByGenreTypeAndCategory(genreType, category))
                        .willReturn(mock(Genre.class));
            }
        }

        List<AdminCastDTO> adminCastDtos = adminContentUpdateRequest.casts();
        for (AdminCastDTO adminCastDto : adminCastDtos) {
            given(adminQuery.findOrSaveCast(eq(adminCastDto.castName()),
                    eq(adminCastDto.castImageUrl())))
                    .willReturn(mock(Cast.class));
        }

        for (String directorName : adminContentUpdateRequest.directors()) {
            given(adminQuery.findOrSaveDirector(
                    eq(directorName)))
                    .willReturn(mock(Director.class));
        }

        for (String countryName : adminContentUpdateRequest.countries()) {
            given(adminQuery.findOrSaveCountry(
                    eq(countryName)))
                    .willReturn(mock(Country.class));
        }

        List<AdminPlatformDTO> adminPlatformDTOS = adminContentUpdateRequest.platforms();
        for (AdminPlatformDTO platDto : adminPlatformDTOS) {
            PlatformType platformType = PlatformType.fromByType(platDto.platformType());
            given(adminQuery.findByPlatform(
                    eq(platformType)))
                    .willReturn(mock(Platform.class));
        }

        // when
        adminService.updateContent(id, adminContentUpdateRequest);

        List<String> categoryTag = adminCategoryDTO.stream().map(AdminCategoryDTO::categoryType)
                .toList();
        List<String> genreTag = adminCategoryDTO.stream().flatMap(dto -> dto.genres().stream())
                .toList();
        List<String> castTag = adminCastDtos.stream().map(AdminCastDTO::castName).toList();
        List<String> directorTag = adminContentUpdateRequest.directors();
        List<String> platformTag = adminPlatformDTOS.stream().map(AdminPlatformDTO::platformType)
                .toList();
        // then
        int genreSize = adminContentUpdateRequest.categories().stream()
                .mapToInt(dto -> dto.genres().size()).sum();
        assertAll(
                () -> verify(adminQuery).findContentByContentId(eq(id)),
                () -> verify(adminQuery).findContentMetadateByContentId(eq(id)),
                () -> verify(content).update(
                        eq(adminContentUpdateRequest.title()),
                        eq(adminContentUpdateRequest.description()),
                        eq(adminContentUpdateRequest.posterUrl()),
                        eq(adminContentUpdateRequest.backdropUrl()),
                        eq(adminContentUpdateRequest.trailerUrl()),
                        eq(adminContentUpdateRequest.openDate()),
                        eq(adminContentUpdateRequest.runningTime()),
                        eq(adminContentUpdateRequest.episode()),
                        eq(adminContentUpdateRequest.rating())),
                () -> verify(adminQuery, times(adminCategoryDTO.size() * 2))
                        .findByCategoryType(any(CategoryType.class)),
                () -> verify(adminQuery, times(genreSize)).findByGenreTypeAndCategory(
                        any(GenreType.class), any(Category.class)
                ),
                () -> verify(adminQuery, times(adminCastDtos.size())).findOrSaveCast(
                        anyString(), anyString()
                ),
                () -> verify(adminQuery,
                        times(adminContentUpdateRequest.directors().size())).findOrSaveDirector(
                        anyString()
                ),
                () -> verify(adminQuery,
                        times(adminContentUpdateRequest.countries().size())).findOrSaveCountry(
                        anyString()
                ),
                () -> verify(adminQuery,
                        times(adminContentUpdateRequest.platforms().size())).findByPlatform(
                        any(PlatformType.class)
                ),
                () -> verify(metadata).update(
                        eq(adminContentUpdateRequest.title()),
                        eq(adminContentUpdateRequest.rating()),
                        eq(categoryTag),
                        eq(genreTag),
                        eq(platformTag),
                        eq(directorTag),
                        eq(castTag)
                )
        );
    }

    @DisplayName("커서 기반 페이지네이션 결과를 반환할 수 있다")
    @Test
    void getContents() {
        // given

        AdminContentGetResponse adminContentGetResponse1 = new AdminContentGetResponse(5L, "T5",
                "p5",
                LocalDateTime.now(), "전체관람가",
                List.of("MOVIE"), List.of("NETFLIX"));
        AdminContentGetResponse adminContentGetResponse = new AdminContentGetResponse(4L, "T4",
                "p4",
                LocalDateTime.now(), "15세",
                List.of("MOVIE", "DRAMA"), List.of("TVING"));
        CursorPageResponse<AdminContentGetResponse> page = new CursorPageResponse<>(
                List.of(adminContentGetResponse1, adminContentGetResponse), "4", true);

        AdminContentGetsRequest adminContentGetsRequest = new AdminContentGetsRequest(5L, 2, null);

        given(contentRepository.getsAdminContents(
                adminContentGetsRequest.cursor(),
                adminContentGetsRequest.size(),
                adminContentGetsRequest.categoryType()
        ))
                .willReturn(page);

        // when
        CursorPageResponse<AdminContentGetResponse> res = adminService.getContents(
                adminContentGetsRequest);

        // then
        assertThat(res).isSameAs(page);
        then(contentRepository).should().getsAdminContents(
                adminContentGetsRequest.cursor(),
                adminContentGetsRequest.size(),
                adminContentGetsRequest.categoryType()
        );
    }


    @DisplayName("커서 기반 카테고리 필터링 페이지네이션 결과를 반환할 수 있다")
    @Test
    void getContentsByCategory() {
        // given
        AdminContentGetResponse adminContentGetResponse1 = new AdminContentGetResponse(4L, "T4",
                "p4",
                LocalDateTime.now(), "15세",
                List.of("DRAMA"), List.of("TVING"));

        AdminContentGetResponse adminContentGetResponse2 = new AdminContentGetResponse(4L, "T4",
                "p4",
                LocalDateTime.now(), "15세",
                List.of("MOVIE", "DRAMA"), List.of("TVING"));

        CursorPageResponse<AdminContentGetResponse> page = new CursorPageResponse<>(
                List.of(adminContentGetResponse2, adminContentGetResponse1), "4", true);

        AdminContentGetsRequest adminContentGetsRequest = new AdminContentGetsRequest(5L, 2, "드라마");

        given(contentRepository.getsAdminContents(
                adminContentGetsRequest.cursor(),
                adminContentGetsRequest.size(),
                adminContentGetsRequest.categoryType()
        ))
                .willReturn(page);

        // when
        CursorPageResponse<AdminContentGetResponse> res = adminService.getContents(
                adminContentGetsRequest);

        // then
        assertThat(res).isSameAs(page);
        then(contentRepository).should().getsAdminContents(
                adminContentGetsRequest.cursor(),
                adminContentGetsRequest.size(),
                adminContentGetsRequest.categoryType()
        );
    }

    @DisplayName("정상 조회 시 필드와 연관관계가 매핑되어 반환될 수 있다.")
    @Test
    void getContentSuccess() {
        // given
        Long id = 100L;
        AdminContentGetDetailResponse contentGetDetailResponse = new AdminContentGetDetailResponse(
                "테스트 제목", "테스트 설명", "https://poster.url", "https://backdrop.url",
                "https://trailer.url",
                LocalDateTime.of(2023, 1, 1, 0, 0), 120, 1, "전체 관람가",
                List.of(new AdminCategoryDTO("영화", List.of("액션"))),
                List.of("한국"),
                List.of("테스트 감독"),
                List.of(new AdminCastDTO("테스트 배우", "https://cast.url")),
                List.of(new AdminPlatformDTO("넷플릭스", "https://platform.url")));

        given(contentRepository.getAdminContentDetails(id)).willReturn(contentGetDetailResponse);

        // when
        AdminContentGetDetailResponse response = adminService.getContent(id);

        // then
        assertAll(
                () -> assertEquals(contentGetDetailResponse.title(), response.title()),
                () -> assertEquals(contentGetDetailResponse.description(), response.description()),
                () -> assertEquals(contentGetDetailResponse.posterUrl(), response.posterUrl()),
                () -> assertEquals(contentGetDetailResponse.backdropUrl(), response.backdropUrl()),
                () -> assertEquals(contentGetDetailResponse.trailerUrl(), response.trailerUrl()),
                () -> assertEquals(contentGetDetailResponse.openDate(), response.openDate()),
                () -> assertEquals(contentGetDetailResponse.runningTime(), response.runningTime()),
                () -> assertEquals(contentGetDetailResponse.episode(), response.episode()),
                () -> assertEquals(contentGetDetailResponse.rating(), response.rating()),
                () -> assertEquals(contentGetDetailResponse.categories(), response.categories()),
                () -> assertEquals(contentGetDetailResponse.countries(), response.countries()),
                () -> assertEquals(contentGetDetailResponse.directors(), response.directors()),
                () -> assertEquals(contentGetDetailResponse.casts(), response.casts()),
                () -> assertEquals(contentGetDetailResponse.platforms(), response.platforms())
        );
    }

    @DisplayName("콘텐츠를 삭제할 때 소프트 딜리트, 콘텐트와 연관 관계는 하드 딜리트될 수 있다.")
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
        assertAll(
                () -> verify(adminQuery).findContentByContentId(eq(id)),
                () -> verify(content).delete(eq(true)),

                () -> verify(contentGenreRepository).deleteAllByContent(content),
                () -> verify(contentCategoryRepository).deleteAllByContent(content),
                () -> verify(contentCastRepository).deleteAllByContent(content),
                () -> verify(contentCountryRepository).deleteAllByContent(content),
                () -> verify(contentPlatformRepository).deleteAllByContent(content),
                () -> verify(contentDirectorRepository).deleteAllByContent(content),

                () -> verify(adminQuery).findContentMetadateByContentId(eq(id)),
                () -> verify(metadata).delete(eq(true))
        );
    }
}
