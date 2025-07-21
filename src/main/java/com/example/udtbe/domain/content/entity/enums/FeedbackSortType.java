package com.example.udtbe.domain.content.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedbackSortType {

    NEWEST("최신순"),
    OLDEST("과거순"),
    ;

    private final String type;

    public static FeedbackSortType from(String value) {
        return Arrays.stream(values())
                .filter(f -> f.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.FEEDBACK_TYPE_BAD_REQUEST));
    }

}
