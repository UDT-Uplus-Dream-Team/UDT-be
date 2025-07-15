package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.member.entity.Member;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class FeedbackFixture {

    public static Feedback feedback(Member member, Content content, FeedbackType type) {
        return Feedback.of(type, false, member, content);
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
