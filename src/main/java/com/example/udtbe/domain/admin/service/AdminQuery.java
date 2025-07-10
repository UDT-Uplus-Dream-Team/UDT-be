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


    public Content findContentByContentId(Long contentId){
        return contentRepository.findById(contentId).orElseThrow( ()->
                new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND)
        );
    }

    public ContentMetadata findContentMetadateByContentId(Long contentId){
        return contentMetadataRepository.findByContent_Id(contentId).orElseThrow( ()->
                new RestApiException(ContentErrorCode.CONTENT_METADATA_NOT_FOUND)
        );
    }

    public Category findByCategoryType(CategoryType categoryType){
        return categoryRepository.findByCategoryType(categoryType).orElseThrow( ()->
                new RestApiException(ContentErrorCode.CATEGORY_NOT_FOUND)
        );
    }

    public Genre findByGenreTypeAndCategory(GenreType genreType, Category ccategory){
        return genreRepository.findByGenreTypeAndCategory(genreType,ccategory).orElseThrow( ()->
                new RestApiException(ContentErrorCode.GENRE_NOT_FOUND)
        );
    }

    public Platform findByPlatform(PlatformType platformType){
        return platformRepository.findByPlatformType(platformType).orElseThrow( ()->
                new RestApiException(ContentErrorCode.PLATFORM_NOT_FOUND)
        );
    }

    public Cast findOrSaveCast(String castName, String castImageUrl){
        return castRepository.findByCastNameAndCastImageUrl(castName, castImageUrl).orElseGet( ()->
                castRepository.save(Cast.of(castName, castImageUrl))
        );
    }

    public Director findOrSaveDirector(String directorName){
        return directorRepository.findByDirectorName(directorName).orElseGet( ()->
                directorRepository.save(Director.of(directorName))
        );
    }

    public Country findOrSaveCountry(String countryName){
        return countryRepository.findByCountryName(countryName).orElseGet( ()->
                countryRepository.save(Country.of(countryName))
        );
    }
}
