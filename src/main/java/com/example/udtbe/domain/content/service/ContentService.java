package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.PopularContentsRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.PopularContentsResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.util.PopularContentStore;
import com.example.udtbe.global.config.WeeklyGenrePolicyProperties;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentQuery contentQuery;
    private final WeeklyGenrePolicyProperties weeklyGenrePolicyProperties;
    private final PopularContentStore popularContentStore;

    @Transactional(readOnly = true)
    public CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request) {
        return contentQuery.getContents(request);
    }

    @Transactional(readOnly = true)
    public ContentDetailsGetResponse getContentDetails(Long contentId) {
        return contentQuery.getContentDetails(contentId);
    }

    public List<WeeklyRecommendedContentsResponse> getWeeklyRecommendedContents(
            WeeklyRecommendationRequest request) {
        List<GenreType> genreTypes = weeklyGenrePolicyProperties.getGenreForToday(
                LocalDate.now().getDayOfWeek());
        return contentQuery.getWeeklyRecommendedContents(request, genreTypes);
    }

    public List<PopularContentsResponse> getPopularContents(PopularContentsRequest request) {
        List<PopularContentsResponse> popularContentsResponses = popularContentStore.get();
        if (popularContentsResponses.size() > request.size()) {
            return popularContentsResponses.subList(0, request.size());
        }

        return popularContentsResponses;
    }
}
