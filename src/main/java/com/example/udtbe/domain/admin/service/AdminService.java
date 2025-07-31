package com.example.udtbe.domain.admin.service;

import com.example.udtbe.domain.admin.dto.AdminContentMapper;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
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
import com.example.udtbe.global.log.annotation.LogReturn;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final ContentMetadataRepository contentMetadataRepository;
    private final ContentRepository contentRepository;
    private final AdminQuery adminQuery;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentCastRepository contentCastRepository;
    private final ContentCountryRepository contentCountryRepository;
    private final ContentPlatformRepository contentPlatformRepository;
    private final ContentDirectorRepository contentDirectorRepository;

    @Transactional
    @LogReturn
    public AdminContentRegisterResponse registerContent(AdminContentRegisterRequest request) {
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
            Director director = adminQuery.findDirector(directorId);
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

        return AdminContentMapper.toContentRegisterResponse(content);
    }

    @Transactional
    @LogReturn
    public void deleteContent(Long contentId) {
        Content content = adminQuery.findContentByContentId(contentId);
        content.delete(true);
        deleteContentRelation(content);
        ContentMetadata contentMetadata = adminQuery.findContentMetadateByContentId(contentId);
        contentMetadata.delete(true);
    }

    @Transactional
    @LogReturn
    public AdminContentUpdateResponse updateContent(Long contentId,
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
            Director director = adminQuery.findDirector(directorId);
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

        return AdminContentMapper.toContentUpdateResponse(content);
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

}
