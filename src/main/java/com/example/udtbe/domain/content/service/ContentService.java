package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.PopularContentsRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.PopularContentsResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.util.PopularContentStore;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.config.WeeklyGenrePolicyProperties;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public void saveCuratedContent(Long contentId, Member member) {
        Optional<CuratedContent> findCuratedContent = contentQuery
                .findCuratedContentByMemberIdAndContentId(member.getId(), contentId);

        if (findCuratedContent.isPresent()) {
            CuratedContent curatedContent = findCuratedContent.get();
            if (!curatedContent.isDeleted()) {
                throw new RestApiException(ContentErrorCode.ALREADY_CURATED_CONTENT);
            }
            curatedContent.reactivate();
        } else {
            Content content = contentQuery.getReferenceById(contentId);
            CuratedContent curatedContent = CuratedContent.of(false, member, content);
            contentQuery.saveCuratedContent(curatedContent);
        }
    }

    @Transactional
    public void deleteCuratedContents(Long memberId, List<Long> contentIds) {
        List<CuratedContent> curatedContents = contentQuery
                .findCuratedContentsByMemberIdAndContentIds(memberId, contentIds);

        curatedContents.stream()
                .filter(content -> !content.isDeleted())
                .forEach(CuratedContent::softDelete);
    }
}
