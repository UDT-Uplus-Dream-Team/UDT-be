package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.entity.enums.GenreType;
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
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;
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

    public CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request) {
        return contentRepository.getContents(request);
    }

    public ContentDetailsGetResponse getContentDetails(Long contentId) {
        return contentRepository.getContentDetails(contentId);
    }

    public List<WeeklyRecommendedContentsResponse> getWeeklyRecommendedContents(
            WeeklyRecommendationRequest request, List<GenreType> genreTypes) {
        return contentRepository.getWeeklyRecommendedContents(request, genreTypes);
    }
}
