package com.example.udtbe.domain.content.service;

import static com.example.udtbe.domain.content.entity.QFeedbackStatistics.feedbackStatistics;

import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.exception.FeedbackErrorCode;
import com.example.udtbe.domain.content.repository.FeedbackStaticsRepository;
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
    private final FeedbackStaticsRepository feedbackStaticsRepository;

    public List<FeedbackStatistics> findByMemberOrThrow(Long memberId) {
        List<FeedbackStatistics> rows =
                feedbackStaticsRepository.findByMemberIdAndIsDeletedFalse(memberId);

        if (rows.isEmpty()) {
            throw new RestApiException(FeedbackErrorCode.FEEDBACK_STATISTICS_NOT_FOUND);
        }
        return rows;
    }

    public List<FeedbackStatistics> findByMember(Long memberId) {
        return jpaQueryFactory
                .selectFrom(feedbackStatistics)
                .where(feedbackStatistics.member.id.eq(memberId),
                        feedbackStatistics.isDeleted.isFalse())
                .fetch();
    }

    public void increaseStatics(Member member, GenreType genre, FeedbackType feedbacktype) {
        feedbackStaticsRepository.changeFeedbackStatics(member, genre, feedbacktype, +1);
    }

    public void decreaseStatics(Member member, GenreType genre, FeedbackType feedbacktype) {
        feedbackStaticsRepository.changeFeedbackStatics(member, genre, feedbacktype, -1);
    }

}
