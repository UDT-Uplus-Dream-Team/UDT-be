package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.repository.CastRepository;
import com.example.udtbe.domain.content.repository.CategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCastRepository;
import com.example.udtbe.domain.content.repository.ContentCategoryRepository;
import com.example.udtbe.domain.content.repository.ContentCountryRepository;
import com.example.udtbe.domain.content.repository.ContentDirectorRepository;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentPlatformRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.CountryRepository;
import com.example.udtbe.domain.content.repository.DirectorRepository;
import com.example.udtbe.domain.content.repository.GenreRepository;
import com.example.udtbe.domain.content.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentQuery {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final DirectorRepository directorRepository;
    private final CastRepository castRepository;
    private final PlatformRepository platformRepository;
    private final ContentCastRepository contentCastRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentCountryRepository contentCountryRepository;
    private final ContentDirectorRepository contentDirectorRepository;
    private final ContentMetadataRepository contentMetadataRepository;
    private final ContentPlatformRepository contentPlatformRepository;

}
