package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.FeedbackStatics;
import com.example.udtbe.domain.content.entity.QFeedbackStatics;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.member.entity.Member;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeedbackStaticsChangerImpl implements FeedbackStaticsChanger {

    private final JPAQueryFactory qf;
    private final EntityManager em;

    @Override
    @Transactional
    public long changeFeedbackStatics(Member member, GenreType genreType, FeedbackType feedbackType,
            int countChange) {
        QFeedbackStatics feedbackStatics = QFeedbackStatics.feedbackStatics;

        NumberPath<Long> target = switch (feedbackType) {
            case LIKE -> feedbackStatics.likeCount;
            case DISLIKE -> feedbackStatics.dislikeCount;
            case UNINTERESTED -> feedbackStatics.uninterestedCount;
        };

        long updated = qf.update(feedbackStatics)
                .set(target, target.add(countChange))
                .where(feedbackStatics.member.eq(member)
                        .and(feedbackStatics.genreType.eq(genreType))
                        .and(feedbackStatics.isDeleted.isFalse()))
                .execute();

        if (updated == 0 && countChange > 0) {
            em.persist(
                    FeedbackStatics.of(genreType,
                            feedbackType == FeedbackType.LIKE ? 1L : 0L,
                            feedbackType == FeedbackType.DISLIKE ? 1L : 0L,
                            feedbackType == FeedbackType.UNINTERESTED ? 1L : 0L,
                            false,
                            member));
        }
        return updated;
    }
}
