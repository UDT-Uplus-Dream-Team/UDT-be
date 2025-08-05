package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.response.AdminContentCategoryMetricResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.PopularContentByPlatformResponse;
import com.example.udtbe.domain.content.dto.response.RecentContentsResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;

public interface ContentRepositoryCustom {

    AdminContentGetDetailResponse getAdminContentDetails(Long contentId);

    CursorPageResponse<AdminContentGetResponse> getsAdminContents(String cursor, int size,
            String categoryType);

    CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request);

    ContentDetailsGetResponse getContentDetails(Long contentId);

    List<WeeklyRecommendedContentsResponse> getWeeklyRecommendedContents(
            WeeklyRecommendationRequest request, List<GenreType> genreTypes);

    List<RecentContentsResponse> getRecentContents(int size);

    List<PopularContentByPlatformResponse> findPopularContentsByPlatform();

    AdminContentCategoryMetricResponse getContentCategoryMetric();
}
