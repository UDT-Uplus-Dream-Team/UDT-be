package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.QFeedback;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.exception.FeedbackErrorCode;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackQuery {

    private final JPAQueryFactory jpaQueryFactory;
    private final ContentRepository contentRepository;
    private final FeedbackRepository feedbackRepository;

    public Content getContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    public Feedback findFeedbackById(Long feedbackId) {
        return feedbackRepository.findFeedbackById(feedbackId)
                .orElseThrow(() -> new RestApiException(FeedbackErrorCode.FEEDBACK_NOT_FOUND));
    }

    public List<Feedback> getFeedbacksByCursor(Member member,
            FeedbackContentGetRequest feedbackContentGetRequest) {

        QFeedback feedback = QFeedback.feedback;

        BooleanExpression baseCondition = feedback.member.eq(member)
                .and(feedback.feedbackType.eq(feedbackContentGetRequest.feedbackType()))
                .and(feedback.isDeleted.isFalse());

        BooleanExpression cursorCondition = null;

        if (feedbackContentGetRequest.cursor() != null) {
            cursorCondition = switch (feedbackContentGetRequest.feedbackSortType()) {
                case NEWEST -> feedback.id.lt(feedbackContentGetRequest.cursor());
                case OLDEST -> feedback.id.gt(feedbackContentGetRequest.cursor());
            };
        }

        OrderSpecifier<?> orderSpecifier = switch (feedbackContentGetRequest.feedbackSortType()) {
            case NEWEST -> feedback.id.desc();
            case OLDEST -> feedback.id.asc();
        };

        return jpaQueryFactory.selectFrom(feedback)
                .where(baseCondition.and(
                        cursorCondition != null ? cursorCondition : Expressions.TRUE))
                .orderBy(orderSpecifier)
                .limit(feedbackContentGetRequest.size())
                .fetch();
    }
}