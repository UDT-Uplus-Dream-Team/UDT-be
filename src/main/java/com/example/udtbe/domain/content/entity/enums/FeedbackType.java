package com.example.udtbe.domain.content.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedbackType {

    LIKE("좋아요"),
    DISLIKE("싫어요"),
    UNINTERESTED("관심없음"),
    ;

    private final String type;

    public static FeedbackType from(String value) {
        return Arrays.stream(values())
                .filter(f -> f.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.FEEDBACK_TYPE_NOT_FOUND));
    }
}
