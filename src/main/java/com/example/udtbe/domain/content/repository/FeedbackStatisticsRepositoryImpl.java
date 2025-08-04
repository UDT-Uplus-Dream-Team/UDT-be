package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QFeedbackStatistics.feedbackStatistics;

import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackStatisticsRepositoryImpl implements FeedbackStatisticsRepositoryCustom {

    private final JPAQueryFactory qf;

    @Override
    public List<FeedbackStatistics> findByMemberIds(List<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return List.of();
        }

        return qf.selectFrom(feedbackStatistics)
                .where(feedbackStatistics.member.id.in(memberIds),
                        feedbackStatistics.isDeleted.isFalse())
                .fetch();
    }

}
