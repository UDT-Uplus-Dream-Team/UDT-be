package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.QFeedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackSortType;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.member.entity.Member;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackQueryDSLImpl implements FeedbackQueryDSL {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Feedback> getFeedbacksByCursor(
            FeedbackContentGetRequest feedbackContentGetRequest, Member member) {
        QFeedback feedback = QFeedback.feedback;

        FeedbackType feedbackType = FeedbackType.from(feedbackContentGetRequest.feedbackType());
        FeedbackSortType feedbackSortType = FeedbackSortType.from(
                feedbackContentGetRequest.feedbackSortType());

        BooleanExpression baseCondition = feedback.member.eq(member)
                .and(feedback.feedbackType.eq(feedbackType))
                .and(feedback.isDeleted.isFalse());

        BooleanExpression cursorCondition = null;

        if (feedbackContentGetRequest.cursor() != null) {
            cursorCondition = switch (feedbackSortType) {
                case NEWEST -> feedback.id.lt(feedbackContentGetRequest.cursor());
                case OLDEST -> feedback.id.gt(feedbackContentGetRequest.cursor());
            };
        }

        OrderSpecifier<?> orderSpecifier = switch (feedbackSortType) {
            case NEWEST -> feedback.id.desc();
            case OLDEST -> feedback.id.asc();
        };

        return jpaQueryFactory.selectFrom(feedback)
                .where(baseCondition.and(
                        cursorCondition != null ? cursorCondition : Expressions.TRUE))
                .orderBy(orderSpecifier)
                .limit(feedbackContentGetRequest.size() + 1)
                .fetch();

    }
}