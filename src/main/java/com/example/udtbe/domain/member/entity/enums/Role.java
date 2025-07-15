package com.example.udtbe.domain.member.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_GUEST("임시회원"),
    ROLE_USER("일반회원"),
    ROLE_ADMIN("관리자");

    private final String role;

    public static Role from(String value) {
        return Arrays.stream(values())
                .filter(r -> r.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.ROLE_NOT_FOUND));
    }
}
