package com.example.udtbe.domain.content.service;

import static com.example.udtbe.domain.content.entity.enums.StatAction.INCREASE;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.example.udtbe.domain.content.event.FeedbackStatEvent;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class FeedbackStatEventHandler {

    private final FeedbackStatisticsQuery feedbackStatisticsQuery;

    public FeedbackStatEventHandler(FeedbackStatisticsQuery feedbackStatisticsQuery) {
        this.feedbackStatisticsQuery = feedbackStatisticsQuery;
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            include = TransientDataAccessException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void handle(FeedbackStatEvent event) {
        if (event.action() == INCREASE) {
            feedbackStatisticsQuery.increaseStatics(event.member(), event.genreType(),
                    event.feedbackType());
        } else {
            feedbackStatisticsQuery.decreaseStatics(event.member(), event.genreType(),
                    event.feedbackType());
        }
    }

}
