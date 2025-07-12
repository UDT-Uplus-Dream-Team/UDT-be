package com.example.udtbe.domain.content.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    MOVIE("영화"),
    DRAMA("드라마"),
    ANIMATION("애니메이션"),
    VARIETY("예능"),
    ;

    private final String type;

    public static CategoryType from(String value) {
        return Arrays.stream(values())
                .filter(c -> c.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.CATEGORY_TYPE_BAD_REQUEST));
    }

    public static CategoryType fromByType(String value) {
        return Arrays.stream(values())
                .filter(c -> c.getType().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.CATEGORY_TYPE_BAD_REQUEST));
    }
}
