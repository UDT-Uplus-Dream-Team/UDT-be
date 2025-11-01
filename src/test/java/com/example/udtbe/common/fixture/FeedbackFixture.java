package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = PRIVATE)
public class FeedbackFixture {

    public static Feedback feedback(Member member, Content content, FeedbackType type) {
        Feedback feedback = Feedback.of(type, false, member, content);
        ReflectionTestUtils.setField(feedback, "updatedAt", LocalDateTime.now());
        return feedback;
    }

    public static Feedback feedbackWithTime(Member member, Content content, FeedbackType type,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        Feedback feedback = Feedback.of(type, false, member, content);
        ReflectionTestUtils.setField(feedback, "createdAt", createdAt);
        ReflectionTestUtils.setField(feedback, "updatedAt", updatedAt);
        return feedback;
    }

    public static Feedback like(Member member, Content content) {
        return feedback(member, content, FeedbackType.LIKE);
    }

    public static Feedback dislike(Member member, Content content) {
        return feedback(member, content, FeedbackType.DISLIKE);
    }

    public static Feedback uninterested(Member member, Content content) {
        return feedback(member, content, FeedbackType.UNINTERESTED);
    }
}
