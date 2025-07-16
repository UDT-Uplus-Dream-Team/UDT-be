package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;

public interface ContentRepositoryCustom {

    CursorPageResponse<ContentDTO> findContentsAdminByCursor(Long cursor, int size);

    CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request);

    ContentDetailsGetResponse getContentDetails(Long contentId);

    List<WeeklyRecommendedContentsResponse> getWeeklyRecommendedContents(
            WeeklyRecommendationRequest request, List<GenreType> genreTypes);
}
