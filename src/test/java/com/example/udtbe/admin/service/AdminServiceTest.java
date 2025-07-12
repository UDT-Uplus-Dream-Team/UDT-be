package com.example.udtbe.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.ContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.ContentGetDetailResponse;
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
import com.example.udtbe.domain.content.exception.ContentErrorCode;
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

    private ContentRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new ContentRegisterRequest(
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
                        new CategoryDTO("영화", List.of("액션", "SF")),
                        new CategoryDTO("애니메이션", List.of("키즈"))
                ),
                List.of("대한민국"),
                List.of("테스트 감독"),
                List.of(
                        new CastDTO("테스트 배우", "https://cast.image1"),
                        new CastDTO("테스트 배우2", "https://cast.image2")
                ),
                List.of(
                        new PlatformDTO("넷플릭스", "https://watch1", true),
                        new PlatformDTO("왓챠", "https://watch2", false)
                )
        );
    }

    @DisplayName("콘텐츠와 메타데이터를 저장한다.")
    @Test
    void contentRegister() {
        // given
        Long id = 42L;
        Content saved = mock(Content.class);
        given(saved.getId()).willReturn(id);
        given(contentRepository.save(any(Content.class))).willReturn(saved);

        List<CategoryDTO> categoryDTOs = registerRequest.categories();

        for (CategoryDTO categoryDTO : categoryDTOs) {
            Category category = mock(Category.class);
            given(adminQuery.findByCategoryType(
                    CategoryType.fromByType(categoryDTO.categoryType())))
                    .willReturn(category);
            for (String genreName : categoryDTO.genres()) {
                GenreType genreType = GenreType.fromByType(genreName);
                given(adminQuery.findByGenreTypeAndCategory(genreType, category))
                        .willReturn(mock(Genre.class));
            }
        }

        List<CastDTO> castDtos = registerRequest.casts();
        for (CastDTO castDto : castDtos) {
            given(adminQuery.findOrSaveCast(eq(castDto.castName()), eq(castDto.castImageUrl())))
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

        List<PlatformDTO> platformDTOs = registerRequest.platforms();
        for (PlatformDTO platDto : platformDTOs) {
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

        int genresSize = categoryDTOs.stream()
                .mapToInt(dto -> dto.genres().size()).sum();

        // then
        assertAll(
                () -> verify(contentRepository).save(any(Content.class)),

                () -> verify(adminQuery, times(registerRequest.categories().size() * 2))
                        .findByCategoryType(any(CategoryType.class)),

                () -> verify(adminQuery, times(genresSize))
                        .findByGenreTypeAndCategory(any(GenreType.class), any(Category.class)),

                () -> verify(adminQuery, times(castDtos.size()))
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

    @DisplayName("저장 요청 분류 타입이 ENUM에 정의되어 있는 분류 타입과 맞지 않으면 404 에러가 발생한다.")
    @Test
    void saveInvalidCategoryType() {
        // given
        CategoryDTO badCategoryDTO = new CategoryDTO("UNKNOWN", List.of());
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
                List.of(badCategoryDTO),
                registerRequest.countries(),
                registerRequest.directors(),
                registerRequest.casts(),
                registerRequest.platforms()
        );

        assertThatThrownBy(() -> adminService.registerContent(badReq))
                .isInstanceOf(RestApiException.class)
                .hasMessage(EnumErrorCode.CATEGORY_TYPE_BAD_REQUEST.getMessage());

    }

    @DisplayName("관리자는 콘텐츠를 업데이트할 때 필드와 메타데이터를 수정한다")
    @Test
    void updateContent() {
        // given
        Long id = 42L;
        Content content = mock(Content.class);
        ContentMetadata metadata = mock(ContentMetadata.class);

        given(adminQuery.findContentByContentId(id)).willReturn(content);
        given(adminQuery.findContentMetadateByContentId(id))
                .willReturn(metadata);

        ContentUpdateRequest contentUpdateRequest = new ContentUpdateRequest(
                "수정 테스트 제목", "수정 테스트 설명",
                "https://new-poster", "https://new-backdrop", "https://new-trailer",
                LocalDateTime.of(2025, 7, 10, 0, 0),
                130, 1, "19세 관람가",
                List.of(new CategoryDTO("애니메이션", List.of("키즈"))),
                List.of("미국"),
                List.of("수정 테스트 감독"),
                List.of(new CastDTO("수정 테스트 배우", "https://new-image")),
                List.of(new PlatformDTO("디즈니+", "https://watch", false))
        );

        List<CategoryDTO> categoryDTO = contentUpdateRequest.categories();
        for (CategoryDTO dto : categoryDTO) {
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

        List<CastDTO> castDtos = contentUpdateRequest.casts();
        for (CastDTO castDto : castDtos) {
            given(adminQuery.findOrSaveCast(eq(castDto.castName()), eq(castDto.castImageUrl())))
                    .willReturn(mock(Cast.class));
        }

        for (String directorName : contentUpdateRequest.directors()) {
            given(adminQuery.findOrSaveDirector(
                    eq(directorName)))
                    .willReturn(mock(Director.class));
        }

        for (String countryName : contentUpdateRequest.countries()) {
            given(adminQuery.findOrSaveCountry(
                    eq(countryName)))
                    .willReturn(mock(Country.class));
        }

        List<PlatformDTO> platformDTOs = contentUpdateRequest.platforms();
        for (PlatformDTO platDto : platformDTOs) {
            PlatformType platformType = PlatformType.fromByType(platDto.platformType());
            given(adminQuery.findByPlatform(
                    eq(platformType)))
                    .willReturn(mock(Platform.class));
        }

        // when
        adminService.updateContent(id, contentUpdateRequest);

        List<String> categoryTag = categoryDTO.stream().map(CategoryDTO::categoryType).toList();
        List<String> genreTag = categoryDTO.stream().flatMap(dto -> dto.genres().stream()).toList();
        List<String> castTag = castDtos.stream().map(CastDTO::castName).toList();
        List<String> directorTag = contentUpdateRequest.directors();
        List<String> platformTag = platformDTOs.stream().map(PlatformDTO::platformType).toList();
        // then
        int genreSize = contentUpdateRequest.categories().stream()
                .mapToInt(dto -> dto.genres().size()).sum();
        assertAll(
                () -> verify(adminQuery).findContentByContentId(eq(id)),
                () -> verify(adminQuery).findContentMetadateByContentId(eq(id)),
                () -> verify(content).update(
                        eq(contentUpdateRequest.title()),
                        eq(contentUpdateRequest.description()),
                        eq(contentUpdateRequest.posterUrl()),
                        eq(contentUpdateRequest.backdropUrl()),
                        eq(contentUpdateRequest.trailerUrl()),
                        eq(contentUpdateRequest.openDate()),
                        eq(contentUpdateRequest.runningTime()),
                        eq(contentUpdateRequest.episode()),
                        eq(contentUpdateRequest.rating())),
                () -> verify(adminQuery, times(categoryDTO.size() * 2))
                        .findByCategoryType(any(CategoryType.class)),
                () -> verify(adminQuery, times(genreSize)).findByGenreTypeAndCategory(
                        any(GenreType.class), any(Category.class)
                ),
                () -> verify(adminQuery, times(castDtos.size())).findOrSaveCast(
                        anyString(), anyString()
                ),
                () -> verify(adminQuery,
                        times(contentUpdateRequest.directors().size())).findOrSaveDirector(
                        anyString()
                ),
                () -> verify(adminQuery,
                        times(contentUpdateRequest.countries().size())).findOrSaveCountry(
                        anyString()
                ),
                () -> verify(adminQuery,
                        times(contentUpdateRequest.platforms().size())).findByPlatform(
                        any(PlatformType.class)
                ),
                () -> verify(metadata).update(
                        eq(contentUpdateRequest.title()),
                        eq(contentUpdateRequest.rating()),
                        eq(categoryTag),
                        eq(genreTag),
                        eq(platformTag),
                        eq(directorTag),
                        eq(castTag)
                )
        );
    }

    @DisplayName("커서 기반 페이지네이션 결과를 반환한다")
    @Test
    void getContents() {
        // given
        ContentDTO contentDTO1 = new ContentDTO(5L, "T5", "p5", LocalDateTime.now(), "전체관람가");
        ContentDTO contentDTO = new ContentDTO(4L, "T4", "p4", LocalDateTime.now(), "15세");
        CursorPageResponse<ContentDTO> page = new CursorPageResponse<>(
                List.of(contentDTO1, contentDTO), "4", true);

        given(contentRepository.findContentsAdminByCursor(5L, 2))
                .willReturn(page);

        // when
        CursorPageResponse<ContentDTO> res = adminService.getContents(5L, 2);

        // then
        assertThat(res).isSameAs(page);
        then(contentRepository).should().findContentsAdminByCursor(5L, 2);
    }

    @DisplayName("getContent: 정상 조회 시 필드와 연관관계가 매핑되어 반환된다")
    @Test
    void getContentSuccess() {
        // given
        Long id = 100L;
        Content content = spy(ContentFixture.content(
                "테스트 제목", "테스트 설명", "영화", "액션", "넷플릭스"
        ));
        given(adminQuery.findContentByContentId(id)).willReturn(content);

        // when
        ContentGetDetailResponse contentGetDetailResponse = adminService.getContent(id);

        // then
        assertAll(
                () -> verify(adminQuery).findContentByContentId(eq(id)),

                () -> assertEquals(content.getTitle(), contentGetDetailResponse.title()),
                () -> assertEquals(content.getDescription(), contentGetDetailResponse.description())
        );
    }

    @DisplayName("콘텐츠를 삭제할 때 소프트 딜리트, 콘텐트와 연관 관계는 하드 딜리트")
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

                () -> verify(contentGenreRepository).deleteAll(anyCollection()),
                () -> verify(contentCategoryRepository).deleteAll(anyCollection()),
                () -> verify(contentCastRepository).deleteAll(anyCollection()),
                () -> verify(contentCountryRepository).deleteAll(anyCollection()),
                () -> verify(contentPlatformRepository).deleteAll(anyCollection()),
                () -> verify(contentDirectorRepository).deleteAll(anyCollection()),

                () -> verify(adminQuery).findContentMetadateByContentId(eq(id)),
                () -> verify(metadata).delete(eq(true))
        );
    }

    @DisplayName("삭제된 콘텐츠 조회 시 404 예외가 발생한다")
    @Test
    void getContentDeletedNotFound() {
        // given
        Long id = 200L;
        Content deleted = spy(ContentFixture.content(
                "삭제 테스트 제목", "삭제 테스트 설명", "영화", "액션", "넷플릭스"
        ));
        given(adminQuery.findContentByContentId(id)).willReturn(deleted);
        given(deleted.isDeleted()).willReturn(true);

        // when / then
        assertThatThrownBy(
                () -> adminService.getContent(id)
        ).isExactlyInstanceOf(RestApiException.class)
                .hasMessage(ContentErrorCode.CONTENT_NOT_FOUND.getMessage());
    }
}
