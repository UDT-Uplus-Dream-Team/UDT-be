package com.example.udtbe.domain.content.event;

import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.StatAction;
import com.example.udtbe.domain.member.entity.Member;

public record FeedbackStatEvent(
        Member member,
        GenreType genreType,
        FeedbackType feedbackType,
        StatAction action
) {

}
