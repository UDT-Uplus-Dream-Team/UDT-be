package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.exception.FeedbackErrorCode;
import com.example.udtbe.domain.content.repository.FeedbackStatisticsRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackStatisticsQuery {

    private final JPAQueryFactory jpaQueryFactory;
    private final FeedbackStatisticsRepository feedbackStatisticsRepository;

    public List<FeedbackStatistics> findByMemberOrThrow(Long memberId) {
        return feedbackStatisticsRepository.findByMemberIdAndIsDeletedFalse(memberId);
    }

    public List<FeedbackStatistics> findByMember(Long memberId) {
        List<FeedbackStatistics> members =
                feedbackStatisticsRepository.findByMemberIdAndIsDeletedFalse(memberId);
        if (members.isEmpty()) {
            throw new RestApiException(FeedbackErrorCode.FEEDBACK_STATISTICS_NOT_FOUND);
        }
        return members;
    }

    public void increaseStatics(Member member, GenreType genre, FeedbackType feedbacktype) {
        feedbackStatisticsRepository.changeFeedbackStatics(member, genre, feedbacktype, +1);
    }

    public void decreaseStatics(Member member, GenreType genre, FeedbackType feedbacktype) {
        feedbackStatisticsRepository.changeFeedbackStatics(member, genre, feedbacktype, -1);
    }

}
