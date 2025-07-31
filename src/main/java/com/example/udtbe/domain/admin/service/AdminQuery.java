package com.example.udtbe.domain.admin.service;

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
import com.example.udtbe.domain.content.repository.CastRepository;
import com.example.udtbe.domain.content.repository.CategoryRepository;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.CountryRepository;
import com.example.udtbe.domain.content.repository.DirectorRepository;
import com.example.udtbe.domain.content.repository.GenreRepository;
import com.example.udtbe.domain.content.repository.PlatformRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminQuery {

    private final ContentRepository contentRepository;
    private final ContentMetadataRepository contentMetadataRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final CastRepository castRepository;
    private final DirectorRepository directorRepository;
    private final CountryRepository countryRepository;


    public Content findContentByContentId(Long contentId) {
        return contentRepository.findById(contentId).orElseThrow(() ->
                new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND)
        );
    }

    public ContentMetadata findContentMetadateByContentId(Long contentId) {
        return contentMetadataRepository.findByContent_Id(contentId).orElseThrow(() ->
                new RestApiException(ContentErrorCode.CONTENT_METADATA_NOT_FOUND)
        );
    }

    public Category findByCategoryType(CategoryType categoryType) {
        return categoryRepository.findByCategoryType(categoryType).orElseThrow(() ->
                new RestApiException(ContentErrorCode.CATEGORY_NOT_FOUND)
        );
    }

    public Genre findByGenreTypeAndCategory(GenreType genreType, Category category) {
        return genreRepository.findByGenreTypeAndCategory(genreType, category).orElseThrow(() ->
                new RestApiException(ContentErrorCode.GENRE_NOT_FOUND)
        );
    }

    public Platform findByPlatform(PlatformType platformType) {
        return platformRepository.findByPlatformType(platformType).orElseThrow(() ->
                new RestApiException(ContentErrorCode.PLATFORM_NOT_FOUND)
        );
    }

    public Director findDirectorByDirectorId(Long directorId) {
        return directorRepository.findById(directorId).orElseThrow(() ->
                new RestApiException(ContentErrorCode.DIRECTOR_NOT_FOUND)
        );
    }

    public Country findOrSaveCountry(String countryName) {
        return countryRepository.findByCountryName(countryName).orElseGet(() ->
                countryRepository.save(Country.of(countryName))
        );
    }

    public Cast findCastByCastId(Long castId) {
        return castRepository.findById(castId).orElseThrow(() ->
                new RestApiException(ContentErrorCode.CAST_NOT_FOUND)
        );
    }

    public List<Cast> saveAllCasts(List<Cast> casts) {
        return castRepository.saveAll(casts);
    }
}
