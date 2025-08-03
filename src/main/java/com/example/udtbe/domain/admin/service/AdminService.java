package com.example.udtbe.domain.admin.service;

import com.example.udtbe.domain.admin.dto.AdminContentMapper;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminMemberGenreFeedbackDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminScheduledContentsRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentCategoryMetricResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentDeleteResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMemberInfoGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentMetricGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResultResponse;
import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchFilterType;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentJobRepositoryImpl;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import com.example.udtbe.domain.batch.repository.JobMetricRepository;
import com.example.udtbe.domain.content.dto.CastMapper;
import com.example.udtbe.domain.content.dto.DirectorMapper;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.ContentPlatform;
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
import com.example.udtbe.domain.content.service.FeedbackStatisticsQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.service.MemberQuery;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.log.annotation.LogReturn;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminContentRegisterJobRepository adminContentRegisterJobRepository;
    private final AdminContentUpdateJobRepository adminContentUpdateJobRepository;
    private final AdminContentDeleteJobRepository adminContentDeleteJobRepository;

    private final ContentMetadataRepository contentMetadataRepository;
    private final ContentRepository contentRepository;
    private final AdminQuery adminQuery;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentCastRepository contentCastRepository;
    private final ContentCountryRepository contentCountryRepository;
    private final ContentPlatformRepository contentPlatformRepository;
    private final ContentDirectorRepository contentDirectorRepository;
    private final MemberQuery memberQuery;
    private final FeedbackStatisticsQuery feedbackStatisticsQuery;
    private final AdminContentMapper adminContentMapper;
    private final AdminContentJobRepositoryImpl adminContentJobRepositoryImpl;
    private final JobMetricRepository jobMetricRepository;


    @Transactional
    @LogReturn
    public AdminContentRegisterResponse registerBulkContent(Member member,
            AdminContentRegisterRequest request) {
        AdminContentRegisterJob job = AdminContentMapper.toContentRegisterJob(request,
                member.getId());

        adminContentRegisterJobRepository.save(job);
        return AdminContentMapper.toContentRegisterResponse(job.getId());
    }

    @Transactional
    @LogReturn
    public AdminContentUpdateResponse updateBulkContent(Member member, Long contentId,
            AdminContentUpdateRequest request) {
        AdminContentUpdateJob job = AdminContentMapper.toContentUpdateJob(request, contentId,
                member.getId());
        adminContentUpdateJobRepository.save(job);
        return AdminContentMapper.toContentUpdateResponse(job.getId());
    }

    @Transactional
    @LogReturn
    public AdminContentDeleteResponse deleteBulkContent(Member member, Long contentId) {
        AdminContentDeleteJob job = AdminContentMapper.toContentDeleteJob(contentId,
                member.getId());
        adminContentDeleteJobRepository.save(job);
        return AdminContentMapper.toContentDeleteResponse(job.getId());
    }


    @LogReturn
    public void registerContent(AdminContentRegisterRequest request) {
        Content content = contentRepository.save(AdminContentMapper.toContentEntity(request));

        request.categories().forEach(dto -> {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(dto.categoryType()));
            ContentCategory.of(content, category);
        });

        List<String> castTags = new ArrayList<>();
        request.casts().forEach(castId -> {
            Cast cast = adminQuery.findCastByCastId(castId);
            ContentCast.of(content, cast);
            castTags.add(cast.getCastName());
        });

        List<String> directorTags = new ArrayList<>();
        request.directors().forEach(directorId -> {
            Director director = adminQuery.findDirectorByDirectorId(directorId);
            ContentDirector.of(content, director);
            directorTags.add(director.getDirectorName());
        });

        request.countries().forEach(name -> {
            Country country = adminQuery.findOrSaveCountry(name);
            ContentCountry.of(content, country);
        });

        request.platforms().forEach(dto -> {
            Platform platform = adminQuery.findByPlatform(
                    PlatformType.fromByType(dto.platformType()));
            ContentPlatform.of(dto.watchUrl(), content, platform);
        });

        for (AdminCategoryDTO catDto : request.categories()) {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(catDto.categoryType()));
            for (String genreName : catDto.genres()) {
                Genre genre = adminQuery.findByGenreTypeAndCategory(GenreType.fromByType(genreName),
                        category);
                ContentGenre.of(content, genre);
            }
        }

        List<String> categoryTags = request.categories().stream()
                .map(AdminCategoryDTO::categoryType)
                .toList();
        List<String> platformTags = request.platforms().stream().map(AdminPlatformDTO::platformType)
                .toList();
        List<String> genreTags = request.categories().stream().flatMap(c -> c.genres().stream())
                .distinct().toList();
        contentMetadataRepository.save(ContentMetadata.of(
                content.getTitle(), content.getRating(), categoryTags,
                genreTags, platformTags, directorTags, castTags,
                content
        ));

    }

    @LogReturn
    public void updateContent(Long contentId,
            AdminContentUpdateRequest request) {
        Content content = adminQuery.findContentByContentId(contentId);

        content.update(request.title(), request.description(), request.posterUrl(),
                request.backdropUrl(), request.trailerUrl(), request.openDate(),
                request.runningTime(), request.episode(), request.rating());

        deleteContentRelation(content);

        request.categories().forEach(dto -> {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(dto.categoryType()));
            ContentCategory.of(content, category);
        });

        List<String> castTags = new ArrayList<>();
        request.casts().forEach(castId -> {
            Cast cast = adminQuery.findCastByCastId(castId);
            ContentCast.of(content, cast);
            castTags.add(cast.getCastName());
        });

        List<String> directorTags = new ArrayList<>();
        request.directors().forEach(directorId -> {
            Director director = adminQuery.findDirectorByDirectorId(directorId);
            ContentDirector.of(content, director);
            directorTags.add(director.getDirectorName());
        });

        request.countries().forEach(name -> {
            Country country = adminQuery.findOrSaveCountry(name);
            ContentCountry.of(content, country);
        });

        request.platforms().forEach(dto -> {
            Platform platform = adminQuery.findByPlatform(
                    PlatformType.fromByType(dto.platformType()));
            ContentPlatform.of(dto.watchUrl(), content, platform);
        });

        for (AdminCategoryDTO catDto : request.categories()) {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(catDto.categoryType()));
            for (String genreName : catDto.genres()) {
                Genre genre = adminQuery.findByGenreTypeAndCategory(GenreType.fromByType(genreName),
                        category);
                ContentGenre.of(content, genre);
            }
        }

        ContentMetadata metadata = adminQuery.findContentMetadateByContentId(contentId);
        List<String> categoryTags = request.categories().stream()
                .map(AdminCategoryDTO::categoryType)
                .toList();
        List<String> genreTags = request.categories().stream().flatMap(dto -> dto.genres().stream())
                .distinct().toList();
        List<String> platformTags = request.platforms().stream().map(AdminPlatformDTO::platformType)
                .toList();
        metadata.update(request.title(), request.rating(), categoryTags, genreTags, platformTags,
                directorTags, castTags);

    }

    @LogReturn
    public void deleteContent(Long contentId) {
        Content content = adminQuery.findAndValidContentByContentId(contentId);
        content.delete(true);
        deleteContentRelation(content);
        ContentMetadata contentMetadata = adminQuery.findContentMetadateByContentId(contentId);
        contentMetadata.delete(true);
    }

    @Transactional(readOnly = true)
    @LogReturn(summaryOnly = true)
    public CursorPageResponse<AdminContentGetResponse> getContents(
            AdminContentGetsRequest adminContentGetsRequest
    ) {
        return contentRepository.getsAdminContents(
                adminContentGetsRequest.cursor(),
                adminContentGetsRequest.size(),
                adminContentGetsRequest.categoryType()
        );
    }

    @Transactional(readOnly = true)
    @LogReturn()
    public AdminContentGetDetailResponse getContent(Long contentId) {
        return contentRepository.getAdminContentDetails(contentId);
    }

    private void deleteContentRelation(Content content) {
        contentGenreRepository.deleteAllByContent(content);
        contentCategoryRepository.deleteAllByContent(content);
        contentCastRepository.deleteAllByContent(content);
        contentCountryRepository.deleteAllByContent(content);
        contentPlatformRepository.deleteAllByContent(content);
        contentDirectorRepository.deleteAllByContent(content);
    }

    @Transactional(readOnly = true)
    public AdminMemberInfoGetResponse getMemberFeedbackInfo(Long memberId) {

        Member member = memberQuery.findMemberById(memberId);

        List<FeedbackStatistics> feedbackInfos = feedbackStatisticsQuery.findByMemberOrThrow(
                memberId);

        List<AdminMemberGenreFeedbackDTO> detail = adminContentMapper.toGenreFeedbackDtoList(
                feedbackInfos);

        long likeSum = feedbackInfos.stream().mapToLong(FeedbackStatistics::getLikeCount).sum();
        long dislikeSum = feedbackInfos.stream().mapToLong(FeedbackStatistics::getDislikeCount)
                .sum();
        long uninterestedSum = feedbackInfos.stream()
                .mapToLong(FeedbackStatistics::getUninterestedCount).sum();

        return new AdminMemberInfoGetResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getLastLoginAt(),
                likeSum,
                dislikeSum,
                uninterestedSum,
                detail
        );
    }

    @Transactional
    public AdminCastsRegisterResponse registerCasts(
            AdminCastsRegisterRequest adminCastsRegisterRequest) {

        List<Cast> casts = new ArrayList<>();
        adminCastsRegisterRequest.adminCastDTOs().stream()
                .forEach(adminCastDTO -> casts.add(CastMapper.toCast(
                                adminCastDTO.castName(),
                                adminCastDTO.castImageUrl()
                        ))
                );

        List<Long> savedCastIds = adminQuery.saveAllCasts(casts).stream()
                .map(Cast::getId)
                .toList();
        return new AdminCastsRegisterResponse(savedCastIds);
    }

    public CursorPageResponse<AdminCastsGetResponse> getCasts(
            AdminCastsGetRequest adminCastsGetRequest) {
        return adminQuery.getCasts(adminCastsGetRequest);
    }

    public AdminDirectorsRegisterResponse registerDirectors(
            AdminDirectorsRegisterRequest adminDirectorsRegisterRequest) {

        List<Director> directors = new ArrayList<>();
        adminDirectorsRegisterRequest.adminDirectorDTOS().stream()
                .forEach(adminDirectorDTO -> directors.add(DirectorMapper.toDirector(
                                adminDirectorDTO.directorName(),
                                adminDirectorDTO.directorImageUrl()
                        ))
                );

        List<Long> savedDirectors = adminQuery.saveAllDirectors(directors).stream()
                .map(Director::getId)
                .toList();

        return new AdminDirectorsRegisterResponse(savedDirectors);
    }

    public CursorPageResponse<AdminDirectorsGetResponse> getDirectors(
            AdminDirectorsGetRequest adminDirectorsGetRequest) {
        return adminQuery.getDirectors(adminDirectorsGetRequest);
    }

    public CursorPageResponse<AdminScheduledContentResponse> getBatchJobs(
            AdminScheduledContentsRequest request) {
        BatchFilterType type = BatchFilterType.from(request.type());
        return adminContentJobRepositoryImpl
                .getJobsByCursor(request.cursor(), request.size(), type);
    }

    @Transactional
    public void updateMetric(BatchJobMetric metric) {
        BatchJobMetric adminContentJobMetric = adminQuery.findAdminContentJobMetric(
                metric.getType());

        adminContentJobMetric.update(metric.getStatus(), metric.getTotalRead(),
                metric.getTotalWrite(), metric.getTotalSkip(), metric.getStartTime(),
                metric.getEndTime());
    }

    @Transactional
    public List<AdminScheduledContentResultResponse> getsScheduledResults() {
        List<BatchJobMetric> metrics = jobMetricRepository.findAllByOrderByIdAsc();

        return metrics.stream().map(m ->
                new AdminScheduledContentResultResponse(
                        m.getId(),
                        m.getType(),
                        m.getStatus(),
                        m.getTotalRead(),
                        m.getTotalWrite(),
                        m.getTotalSkip(),
                        m.getStartTime(),
                        m.getEndTime()
                )
        ).toList();
    }

    @Transactional
    public AdminScheduledContentMetricGetResponse getScheduledMetric() {
        List<BatchJobMetric> metrics = jobMetricRepository.findAll();

        long totalRead = metrics.stream().mapToLong(BatchJobMetric::getTotalRead).sum();
        long totalWrite = metrics.stream().mapToLong(BatchJobMetric::getTotalWrite).sum();
        long totalSkip = metrics.stream().mapToLong(BatchJobMetric::getTotalSkip).sum();

        return new AdminScheduledContentMetricGetResponse(totalRead, totalWrite, totalSkip);
  
    public AdminContentCategoryMetricResponse getContentCategoryMetric() {
      return adminQuery.getContentCategoryMetric();
    }

}
