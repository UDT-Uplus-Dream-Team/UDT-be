package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.example.udtbe.domain.content.entity.QFeedbackStatistics;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.member.entity.Member;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackStatisticsChangerImpl implements FeedbackStatisticsChanger {

    private final JPAQueryFactory qf;
    private final EntityManager em;

    @Override
    @Transactional
    public long changeFeedbackStatics(Member member, GenreType genreType, FeedbackType feedbackType,
            int countChange) {
        QFeedbackStatistics feedbackStatistics = QFeedbackStatistics.feedbackStatistics;

        NumberPath<Integer> target = switch (feedbackType) {
            case LIKE -> feedbackStatistics.likeCount;
            case DISLIKE -> feedbackStatistics.dislikeCount;
            case UNINTERESTED -> feedbackStatistics.uninterestedCount;
        };

        long updated = qf.update(feedbackStatistics)
                .set(target, target.add(countChange))
                .where(feedbackStatistics.member.eq(member)
                        .and(feedbackStatistics.genreType.eq(genreType))
                        .and(feedbackStatistics.isDeleted.isFalse()))
                .execute();

        if (updated == 0 && countChange > 0) {
            em.persist(
                    FeedbackStatistics.of(genreType,
                            feedbackType == FeedbackType.LIKE ? 1 : 0,
                            feedbackType == FeedbackType.DISLIKE ? 1 : 0,
                            feedbackType == FeedbackType.UNINTERESTED ? 1 : 0,
                            false,
                            member));
        }
        return updated;
    }
}
