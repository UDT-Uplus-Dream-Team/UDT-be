package com.example.udtbe.domain.admin.service;

import com.example.udtbe.domain.admin.dto.ContentMapper;
import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.ContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.ContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.ContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.ContentUpdateResponse;
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
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentCastRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCountryRepository;
import com.example.udtbe.domain.content.repository.ContentDirectorRepository;
import com.example.udtbe.domain.content.repository.ContentGenreRepository;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentPlatformRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.GenreRepository;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final ContentMetadataRepository contentMetadataRepository;
    private final ContentRepository contentRepository;
    private final AdminQuery adminQuery;
    private final GenreRepository genreRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentCastRepository contentCastRepository;
    private final ContentCountryRepository contentCountryRepository;
    private final ContentPlatformRepository contentPlatformRepository;
    private final ContentDirectorRepository contentDirectorRepository;

    @Transactional
    public ContentRegisterResponse contentRegister(ContentRegisterRequest request) {
        Content content = contentRepository.save(ContentMapper.toContentEntity(request));

        request.categories().forEach(dto -> {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(dto.categoryType()));
            ContentCategory.of(content, category);
        });

        request.casts().forEach(dto -> {
            Cast cast = adminQuery.findOrSaveCast(dto.castName(), dto.castImageUrl());
            ContentCast.of(content, cast);
        });

        request.directors().forEach(name -> {
            Director director = adminQuery.findOrSaveDirector(name);
            ContentDirector.of(content, director);
        });

        request.countries().forEach(name -> {
            Country country = adminQuery.findOrSaveCountry(name);
            ContentCountry.of(content, country);
        });

        request.platforms().forEach(dto -> {
            Platform platform = adminQuery.findByPlatform(
                    PlatformType.fromByType(dto.platformType()));
            ContentPlatform.of(dto.watchUrl(), dto.isAvailable(), content, platform);
        });

        for (CategoryDTO catDto : request.categories()) {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(catDto.categoryType()));
            for (String genreName : catDto.genres()) {
                Genre genre = adminQuery.findByGenreTypeAndCategory(GenreType.fromByType(genreName),
                        category);
                ContentGenre.of(content, genre);
            }
        }

        List<String> categoryTags = request.categories().stream().map(CategoryDTO::categoryType)
                .toList();
        List<String> platformTags = request.platforms().stream().map(PlatformDTO::platformType)
                .toList();
        List<String> genreTags = request.categories().stream().flatMap(c -> c.genres().stream())
                .distinct().toList();
        List<String> castTags = request.casts().stream().map(CastDTO::castName).toList();
        contentMetadataRepository.save(ContentMetadata.of(
                content.getTitle(), content.getRating(), categoryTags,
                genreTags, platformTags, request.directors(), castTags,
                content
        ));

        return ContentMapper.toContentRegisterResponse(content);
    }

    @Transactional
    public void deleteContent(Long contentId) {
        Content content = adminQuery.findContentByContentId(contentId);
        content.delete(true);
        deleteContentRelation(content);
        ContentMetadata contentMetadata = adminQuery.findContentMetadateByContentId(contentId);
        contentMetadata.delete(true);
    }

    @Transactional
    public ContentUpdateResponse updateContent(Long contentId, ContentUpdateRequest request) {
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

        request.casts().forEach(dto -> {
            Cast cast = adminQuery.findOrSaveCast(dto.castName(), dto.castImageUrl());
            ContentCast.of(content, cast);
        });

        request.directors().forEach(name -> {
            Director director = adminQuery.findOrSaveDirector(name);
            ContentDirector.of(content, director);
        });

        request.countries().forEach(name -> {
            Country country = adminQuery.findOrSaveCountry(name);
            ContentCountry.of(content, country);
        });

        request.platforms().forEach(dto -> {
            Platform platform = adminQuery.findByPlatform(
                    PlatformType.fromByType(dto.platformType()));
            ContentPlatform.of(dto.watchUrl(), dto.isAvailable(), content, platform);
        });

        for (CategoryDTO catDto : request.categories()) {
            Category category = adminQuery.findByCategoryType(
                    CategoryType.fromByType(catDto.categoryType()));
            for (String genreName : catDto.genres()) {
                Genre genre = adminQuery.findByGenreTypeAndCategory(GenreType.fromByType(genreName),
                        category);
                ContentGenre.of(content, genre);
            }
        }

        ContentMetadata metadata = adminQuery.findContentMetadateByContentId(contentId);
        List<String> categoryTags = request.categories().stream().map(CategoryDTO::categoryType)
                .toList();
        List<String> genreTags = request.categories().stream().flatMap(dto -> dto.genres().stream())
                .distinct().toList();
        List<String> platformTags = request.platforms().stream().map(PlatformDTO::platformType)
                .toList();
        List<String> castTags = request.casts().stream().map(CastDTO::castName).toList();
        metadata.update(request.title(), request.rating(), categoryTags, genreTags, platformTags,
                request.directors(), castTags);

        return ContentMapper.toContentUpdateResponse(content);
    }

    private void deleteContentRelation(Content content) {
        contentGenreRepository.deleteAll(content.getContentGenres());
        contentCategoryRepository.deleteAll(content.getContentCategories());
        contentCastRepository.deleteAll(content.getContentCasts());
        contentCountryRepository.deleteAll(content.getContentCountries());
        contentPlatformRepository.deleteAll(content.getContentPlatforms());
        contentDirectorRepository.deleteAll(content.getContentDirectors());
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<ContentDTO> getContents(Long cursor, int size) {
        return contentRepository.findContentsAdminByCursor(cursor, size);
    }

    @Transactional(readOnly = true)
    public ContentGetDetailResponse getContent(Long contentId) {
        Content content = adminQuery.findContentByContentId(contentId);
        if (content.isDeleted()) {
            throw new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND);
        }

        Map<String, List<String>> categoryGenreMap = content.getContentGenres().stream()
                .collect(
                        Collectors.groupingBy(
                                g -> g.getGenre().getCategory().getCategoryType().getType(),
                                Collectors.mapping(g -> g.getGenre().getGenreType().getType(),
                                        Collectors.toList())
                        )
                );

        content.getContentCategories().stream()
                .map(c -> c.getCategory().getCategoryType().getType())
                .distinct()
                .forEach(type -> categoryGenreMap.putIfAbsent(type, new ArrayList<>()));

        List<CategoryDTO> categories = categoryGenreMap.entrySet().stream()
                .map(e -> new CategoryDTO(e.getKey(), e.getValue()))
                .toList();

        List<CastDTO> castDTOs = content.getContentCasts().stream()
                .map(c -> new CastDTO(
                        c.getCast().getCastName(),
                        c.getCast().getCastImageUrl()))
                .toList();

        List<String> directors = content.getContentDirectors().stream()
                .map(d -> d.getDirector().getDirectorName())
                .toList();

        List<String> countries = content.getContentCountries().stream()
                .map(c -> c.getCountry().getCountryName())
                .toList();

        List<PlatformDTO> platforms = content.getContentPlatforms().stream()
                .map(p -> new PlatformDTO(
                        p.getPlatform().getPlatformType().getType(),
                        p.getWatchUrl(),
                        p.isAvailable()))
                .toList();

        return ContentMapper.toContentGetResponse(content, categories, castDTOs, directors,
                countries, platforms);
    }

}
