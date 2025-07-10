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
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
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

    // Write 파트

    // 콘텐츠 생성
    @Transactional
    public ContentRegisterResponse contentRegister(ContentRegisterRequest request) {
        Content content = contentRepository.save(ContentMapper.toContentEntity(request));

        // 모든 관련 엔티티 생성 및 저장
        request.categories().forEach(dto -> {
            Category category = adminQuery.findByCategoryType(CategoryType.fromByType(dto.categoryType()));
            ContentCategory.of().addContentAndCategory(content, category);
        });

        request.casts().forEach(dto -> {
            Cast cast = adminQuery.findOrSaveCast(dto.castName(), dto.castImageUrl());
            ContentCast.of().addContentAndCast(content, cast);
        });

        request.directors().forEach(name -> {
            Director director = adminQuery.findOrSaveDirector(name);
            ContentDirector.of().addContentAndDirector(content, director);
        });

        request.countries().forEach(name -> {
            Country country = adminQuery.findOrSaveCountry(name);
            ContentCountry.of().addContentAndCountry(content, country);
        });

        request.platforms().forEach(dto -> {
            Platform platform = adminQuery.findByPlatform(PlatformType.fromByType(dto.platformType()));
            ContentPlatform.of(dto.watchUrl(), dto.isAvailable()).addContentAndPlatform(content, platform);
        });

        // 장르 처리
        request.categories().stream()
                .flatMap(c -> adminQuery.findByCategoryType(CategoryType.fromByType(c.categoryType())).getGenres().stream())
                .forEach(genre -> ContentGenre.of().addContentAndGenre(content, genre));

        // 메타데이터 저장
        List<String> platformTags = request.platforms().stream().map(PlatformDTO::platformType).toList();
        List<String> genreTags = request.categories().stream().flatMap(c -> c.genres().stream()).distinct().toList();
        contentMetadataRepository.save(ContentMetadata.of(
                content.getTitle(), content.getRating(),
                platformTags, request.directors(), genreTags,
                content
        ));

        return ContentMapper.toContentRegisterResponse(content);
    }

    // 관리자의 콘텐츠 삭제 (soft-delete)
    @Transactional
    public void deleteContent(Long contentId){
        Content content = adminQuery.findContentByContentId(contentId);
        content.delete(true);
        content.clearAllRelations();
        ContentMetadata contentMetadata = adminQuery.findContentMetadateByContentId(contentId);
        contentMetadata.delete(true);
    }

    @Transactional
    public void updateContent(Long contentId, ContentUpdateRequest request) {
        Content content = adminQuery.findContentByContentId(contentId);

        // 기본 필드 업데이트
        content.update(request.title(), request.description(), request.posterUrl(),
                request.backdropUrl(), request.trailerUrl(), request.openDate(),
                request.runningTime(), request.episode(), request.rating());

        // 기존 관계들 제거 후 새로 생성
        content.clearAllRelations();

        // 새로운 관계들 생성
        request.categories().forEach(dto -> {
            Category category = adminQuery.findByCategoryType(CategoryType.fromByType(dto.categoryType()));
            ContentCategory.of().addContentAndCategory(content, category);
        });

        request.casts().forEach(dto -> {
            Cast cast = adminQuery.findOrSaveCast(dto.castName(), dto.castImageUrl());
            ContentCast.of().addContentAndCast(content, cast);
        });

        request.directors().forEach(name -> {
            Director director = adminQuery.findOrSaveDirector(name);
            ContentDirector.of().addContentAndDirector(content, director);
        });

        request.countries().forEach(name -> {
            Country country = adminQuery.findOrSaveCountry(name);
            ContentCountry.of().addContentAndCountry(content, country);
        });

        request.platforms().forEach(dto -> {
            Platform platform = adminQuery.findByPlatform(PlatformType.fromByType(dto.platformType()));
            ContentPlatform.of(dto.watchUrl(), dto.isAvailable()).addContentAndPlatform(content, platform);
        });

        // 장르 처리
        request.categories().stream()
                .flatMap(c -> adminQuery.findByCategoryType(CategoryType.fromByType(c.categoryType())).getGenres().stream())
                .distinct()
                .forEach(genre -> ContentGenre.of().addContentAndGenre(content, genre));

        // 메타데이터 업데이트
        ContentMetadata metadata = adminQuery.findContentMetadateByContentId(contentId);
        List<String> genreTags = request.categories().stream().flatMap(dto -> dto.genres().stream()).distinct().toList();
        List<String> platformTags = request.platforms().stream().map(PlatformDTO::platformType).toList();

        metadata.update(request.title(), request.rating(), platformTags, request.directors(), genreTags);
    }

    // Read 파트
    // 관리자의 콘텐츠 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<ContentDTO> getContents(Long cursor, int size){
        return contentRepository.findContentsAdminByCursor(cursor,size);
    }

    // 관리자의 콘텐츠 조회
    @Transactional(readOnly = true)
    public ContentGetDetailResponse getContent(Long contentId) {
        Content content = adminQuery.findContentByContentId(contentId);
        if (content.isDeleted()) {
            throw new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND);
        }

        // 1) 카테고리 + 장르 매핑
        Map<String, List<String>> categoryGenreMap = content.getContentGenres().stream()
                .filter(g -> !g.isDeleted())
                .collect(
                        Collectors.groupingBy(
                                g -> g.getGenre().getCategory().getCategoryType().getType(),
                                Collectors.mapping(g -> g.getGenre().getGenreType().getType(), Collectors.toList())
                        )
                );

        content.getContentCategories().stream()
                .filter(c -> !c.isDeleted())
                .map(c -> c.getCategory().getCategoryType().getType())
                .distinct()
                .forEach(type -> categoryGenreMap.putIfAbsent(type, new ArrayList<>()));

        List<CategoryDTO> categories = categoryGenreMap.entrySet().stream()
                .map(e -> new CategoryDTO(e.getKey(), e.getValue()))
                .toList();

        // 2) 출연진
        List<CastDTO> castDTOs = content.getContentCasts().stream()
                .filter(c -> !c.isDeleted())
                .map(c -> new CastDTO(
                        c.getCast().getCastName(),
                        c.getCast().getCastImageUrl()))
                .toList();

        // 3) 감독
        List<String> directors = content.getContentDirectors().stream()
                .filter(d -> !d.isDeleted())
                .map(d -> d.getDirector().getDirectorName())
                .toList();

        // 4) 국가
        List<String> countries = content.getContentCountries().stream()
                .filter(c -> !c.isDeleted())
                .map(c -> c.getCountry().getCountryName())
                .toList();

        // 5) 플랫폼
        List<PlatformDTO> platforms = content.getContentPlatforms().stream()
                .filter(p -> !p.isDeleted())
                .map(p -> new PlatformDTO(
                        p.getPlatform().getPlatformType().getType(),
                        p.getWatchUrl(),
                        p.isAvailable()))
                .toList();

        return ContentMapper.toContentGetResponse(content, categories, castDTOs, directors, countries, platforms);
    }


}
