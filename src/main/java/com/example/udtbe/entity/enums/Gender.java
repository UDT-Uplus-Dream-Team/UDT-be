package com.example.udtbe.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MAN("남자"),
    WOMAN("여자"),
    ;

    private final String gender;

    public static Gender from(String value) {
        return Arrays.stream(values())
                .filter(r -> r.getGender().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.GENDER_NOT_FOUND));
    }
}
