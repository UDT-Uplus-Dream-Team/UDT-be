package com.example.udtbe.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.BatchJobMetricFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.admin.dto.common.AdminCastDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCastDetailsDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminDirectorDTO;
import com.example.udtbe.domain.admin.dto.common.AdminDirectorDetailsDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminMemberListGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminScheduledContentsRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMemberInfoGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMembersGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentMetricGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResultResponse;
import com.example.udtbe.domain.admin.service.AdminQuery;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchFilterType;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.repository.AdminContentJobRepositoryImpl;
import com.example.udtbe.domain.batch.repository.JobMetricRepository;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.FeedbackStatistics;
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
import com.example.udtbe.domain.content.repository.FeedbackStatisticsRepository;
import com.example.udtbe.domain.content.repository.FeedbackStatisticsRepositoryImpl;
import com.example.udtbe.domain.content.service.FeedbackStatisticsQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.domain.member.service.MemberQuery;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    @Mock
    private MemberQuery memberQuery;
    @Mock
    private FeedbackStatisticsQuery feedbackStatisticsQuery;
    @Mock
    private AdminContentJobRepositoryImpl adminContentJobRepositoryImpl;
    @Mock
    private FeedbackStatisticsRepositoryImpl feedbackStatisticsRepositoryImpl;
    @Mock
    private FeedbackStatisticsRepository feedbackStatisticsRepository;
    @Mock
    private JobMetricRepository jobMetricRepository;

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
                List.of(1L, 2L),
                List.of(1L, 2L),
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

        given(adminQuery.findCastByCastId(anyLong())).willReturn(mock(Cast.class));
        given(adminQuery.findDirectorByDirectorId(anyLong())).willReturn(mock(Director.class));

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

                () -> verify(adminQuery, times(registerRequest.casts().size()))
                        .findCastByCastId(anyLong()),

                () -> verify(adminQuery, times(registerRequest.directors().size()))
                        .findDirectorByDirectorId(anyLong()),

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

    @DisplayName("콘텐츠를 업데이트할 때 필드와 메타데이터를 수정할 수 있다.")
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
                List.of(1L, 2L),
                List.of(1L, 2L),
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

        given(adminQuery.findCastByCastId(anyLong())).willReturn(mock(Cast.class));
        given(adminQuery.findDirectorByDirectorId(anyLong())).willReturn(mock(Director.class));

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
                () -> verify(adminQuery, times(registerRequest.casts().size())).findCastByCastId(
                        anyLong()
                ),
                () -> verify(adminQuery,
                        times(adminContentUpdateRequest.directors()
                                .size())).findDirectorByDirectorId(anyLong()
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
                        any(List.class),
                        any(List.class)
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

        AdminContentGetsRequest adminContentGetsRequest = new AdminContentGetsRequest(
                "5|2025-07-30", 2, null);

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

        AdminContentGetsRequest adminContentGetsRequest = new AdminContentGetsRequest(
                "5|2025-07-05", 2, "드라마");

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
                List.of(new AdminDirectorDetailsDTO(1L, "봉준호", "봉준호@director")),
                List.of(new AdminCastDetailsDTO(1L, "이병헌", "이병헌@cast")),
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
        given(adminQuery.findAndValidContentByContentId(id)).willReturn(content);
        given(adminQuery.findContentMetadateByContentId(id))
                .willReturn(metadata);

        // when
        adminService.deleteContent(id);

        // then
        assertAll(
                () -> verify(adminQuery).findAndValidContentByContentId(eq(id)),
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

    @DisplayName("관리자는 유저의 피드백 상세 지표를 조회할 수 있다.")
    @Test
    void getMemberFeedbackStatistics() {
        // given
        Long memberId = 1L;
        Member member = MemberFixture.member("hong@test.com", Role.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", memberId);

        given(memberQuery.findMemberById(memberId)).willReturn(member);

        FeedbackStatistics dramaStat = FeedbackStatistics.of(
                GenreType.DRAMA, 4, 1, 0, false, member
        );

        FeedbackStatistics actionStat = FeedbackStatistics.of(
                GenreType.ACTION, 3, 2, 1, false, member
        );

        given(feedbackStatisticsQuery.findByMemberOrThrow(memberId))
                .willReturn(List.of(dramaStat, actionStat));

        // when
        AdminMemberInfoGetResponse response = adminService.getMemberFeedbackInfo(memberId);

        // then
        assertAll(
                () -> assertThat(response.id()).isEqualTo(memberId),
                () -> assertThat(response.name()).isEqualTo(member.getName()),
                () -> assertThat(response.totalLikeCount()).isEqualTo(7),
                () -> assertThat(response.totalDislikeCount()).isEqualTo(3),
                () -> assertThat(response.totalUninterestedCount()).isEqualTo(1),
                () -> assertThat(response.genres()).hasSize(2)
                        .extracting("genreType")
                        .containsExactlyInAnyOrder(GenreType.DRAMA, GenreType.ACTION)
        );

        then(memberQuery).should().findMemberById(memberId);
        then(feedbackStatisticsQuery).should().findByMemberOrThrow(memberId);
    }


    @DisplayName("유저 정보 목록을 무한 스크롤로 조회할 수 있다.")
    @Test
    void getMemberList() {
        // given
        AdminMemberListGetRequest req = new AdminMemberListGetRequest(
                null,
                3,
                null
        );

        Member member1 = MemberFixture.member("member1@test.com", Role.ROLE_USER);
        Member member2 = MemberFixture.member("member2@test.com", Role.ROLE_USER);
        Member member3 = MemberFixture.member("member3@test.com", Role.ROLE_USER);

        ReflectionTestUtils.setField(member1, "id", 3L);
        ReflectionTestUtils.setField(member2, "id", 2L);
        ReflectionTestUtils.setField(member3, "id", 1L);

        given(memberQuery.findMembersForAdmin(req.cursor(), req.size() + 1, req.keyword()))
                .willReturn(List.of(member1, member2, member3));

        given(feedbackStatisticsRepositoryImpl.findByMemberIds(List.of(3L, 2L, 1L)))
                .willReturn(List.of(
                        FeedbackStatistics.of(GenreType.ACTION, 1, 0, 0, false, member1),
                        FeedbackStatistics.of(GenreType.DRAMA, 2, 1, 0, false, member1),
                        FeedbackStatistics.of(GenreType.DRAMA, 1, 0, 1, false, member2)
                ));

        // when
        CursorPageResponse<AdminMembersGetResponse> res = adminService.getMembers(req);

        // then
        assertThat(res.item()).hasSize(3);
        assertThat(res.hasNext()).isFalse();
        assertThat(res.nextCursor()).isEqualTo(null);

        AdminMembersGetResponse dto1 = res.item().get(0);
        assertThat(dto1.id()).isEqualTo(3L);
        assertThat(dto1.totalLikeCount()).isEqualTo(3);
        assertThat(dto1.totalDislikeCount()).isEqualTo(1);
        assertThat(dto1.totalUninterestedCount()).isZero();

        verify(memberQuery, times(1))
                .findMembersForAdmin(req.cursor(), req.size() + 1, req.keyword());
    }

    @DisplayName("여러 명의 출연진을 한번에 저장한다.")
    @Test
    void registerCasts() {

        // given
        final AdminCastDTO adminCastDTO1 = new AdminCastDTO("강호동", "강호동.image.com");
        final AdminCastDTO adminCastDTO2 = new AdminCastDTO("유재석", "유재석.image.com");
        final AdminCastDTO adminCastDTO3 = new AdminCastDTO("이효리", "이효리.image.com");
        AdminCastsRegisterRequest request = new AdminCastsRegisterRequest(
                List.of(adminCastDTO1, adminCastDTO2, adminCastDTO3)
        );

        Cast cast1 = mock(Cast.class);
        Cast cast2 = mock(Cast.class);
        Cast cast3 = mock(Cast.class);

        given(cast1.getId()).willReturn(1L);
        given(cast2.getId()).willReturn(2L);
        given(cast3.getId()).willReturn(3L);

        given(adminQuery.saveAllCasts(any(List.class))).willReturn(List.of(cast1, cast2, cast3));

        // when
        AdminCastsRegisterResponse response = adminService.registerCasts(request);

        // then
        assertAll(
                () -> verify(adminQuery).saveAllCasts(any(List.class)),
                () -> assertThat(response.castIds()).containsExactly(
                        cast1.getId(),
                        cast2.getId(),
                        cast3.getId()
                )
        );
    }

    @DisplayName("여러 명의 감독을 한번에 저장한다.")
    @Test
    void registerDirectors() {
        // given
        final AdminDirectorDTO adminDirectorDTO1 = new AdminDirectorDTO("봉준호", "봉준호.image.com");
        final AdminDirectorDTO adminDirectorDTO2 = new AdminDirectorDTO("박찬욱", "박찬욱.image.com");
        final AdminDirectorDTO adminDirectorDTO3 = new AdminDirectorDTO("류승완", "류승완.image.com");
        AdminDirectorsRegisterRequest request = new AdminDirectorsRegisterRequest(
                List.of(adminDirectorDTO1, adminDirectorDTO2, adminDirectorDTO3)
        );

        Director director1 = mock(Director.class);
        Director director2 = mock(Director.class);
        Director director3 = mock(Director.class);

        given(director1.getId()).willReturn(1L);
        given(director2.getId()).willReturn(2L);
        given(director3.getId()).willReturn(3L);

        given(adminQuery.saveAllDirectors(any(List.class)))
                .willReturn(List.of(director1, director2, director3));

        // when
        AdminDirectorsRegisterResponse response = adminService.registerDirectors(request);

        // then
        assertAll(
                () -> verify(adminQuery).saveAllDirectors(any(List.class)),
                () -> assertThat(response.directorIds()).containsExactly(
                        director1.getId(),
                        director2.getId(),
                        director3.getId()
                )
        );
    }

    @DisplayName("배치 작업 목록을 커서 기반 페이지네이션으로 조회할 수 있다.")
    @Test
    void getBatchJobs() {
        // given
        AdminScheduledContentsRequest request = new AdminScheduledContentsRequest("5", 10,
                "FAILED");
        BatchFilterType type = BatchFilterType.from(request.type());

        List<AdminScheduledContentResponse> jobs = List.of(
                new AdminScheduledContentResponse(5L, BatchStatus.PENDING, 1L,
                        LocalDateTime.now(),
                        LocalDateTime.now(), LocalDateTime.now(), BatchJobType.DELETE),
                new AdminScheduledContentResponse(4L, BatchStatus.FAILED, 1L,
                        LocalDateTime.now(),
                        LocalDateTime.now(), LocalDateTime.now(), BatchJobType.DELETE)
        );
        CursorPageResponse<AdminScheduledContentResponse> expectedResponse = new CursorPageResponse<>(
                jobs, "4", true);

        given(adminContentJobRepositoryImpl.getJobsByCursor(request.cursor(), request.size(),
                type))
                .willReturn(expectedResponse);

        // when
        CursorPageResponse<AdminScheduledContentResponse> actualResponse = adminService.getBatchJobs(
                request);

        // then
        assertThat(actualResponse).isSameAs(expectedResponse);
        then(adminContentJobRepositoryImpl).should()
                .getJobsByCursor(request.cursor(), request.size(), type);
    }


    @DisplayName("배치 작업의 메트릭을 업데이트할 수 있다.")
    @Test
    void updateMetric() {
        // given
        BatchJobMetric metric = BatchJobMetricFixture.completedJob(1L,
                BatchJobType.DELETE, 100);
        BatchJobMetric findMetric = mock(BatchJobMetric.class);

        given(adminQuery.findAdminContentJobMetric(metric.getType())).willReturn(findMetric);

        // when
        adminService.updateMetric(metric);

        // then
        verify(findMetric).update(metric.getStatus(), metric.getTotalRead(),
                metric.getTotalWrite(), metric.getTotalSkip(), metric.getStartTime(),
                metric.getEndTime());
    }

    @DisplayName("배치 작업 결과 목록을 조회할 수 있다.")
    @Test
    void getsScheduledResults() {
        // given
        BatchJobMetric metric1 = BatchJobMetricFixture.completedJob(1L,
                BatchJobType.REGISTER, 100);
        BatchJobMetric metric2 = BatchJobMetricFixture.partialCompetedJob(2L,
                BatchJobType.UPDATE, 100, 40);
        List<BatchJobMetric> metrics = List.of(metric1, metric2);
        given(jobMetricRepository.findAllByOrderByIdAsc()).willReturn(metrics);

        // when
        List<AdminScheduledContentResultResponse> responses = adminService.getsScheduledResults();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).resultId()).isEqualTo(metric1.getId());
        assertThat(responses.get(1).resultId()).isEqualTo(metric2.getId());
    }

    @DisplayName("배치 작업 메트릭을 조회할 수 있다.")
    @Test
    void getScheduledMetric() {
        // given
        BatchJobMetric metric1 = BatchJobMetricFixture.completedJob(1L,
                BatchJobType.DELETE, 100);

        BatchJobMetric metric2 = BatchJobMetricFixture.partialCompetedJob(2L,
                BatchJobType.UPDATE, 50, 10);

        List<BatchJobMetric> metrics = List.of(metric1, metric2);
        given(jobMetricRepository.findAll()).willReturn(metrics);

        // when
        AdminScheduledContentMetricGetResponse response = adminService.getScheduledMetric();

        // then
        assertThat(response.totalRead()).isEqualTo(metric1.getTotalRead() + metric2.getTotalRead());
        assertThat(response.totalWrite()).isEqualTo(
                metric1.getTotalWrite() + metric2.getTotalWrite());
        assertThat(response.totalSkip()).isEqualTo(metric1.getTotalSkip() + metric2.getTotalSkip());
    }
}

