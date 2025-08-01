package com.example.udtbe.domain.content.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlatformType {

    NETFLIX("넷플릭스"),
    TVING("티빙"),
    COUPANG_PLAY("쿠팡플레이"),
    WAVVE("웨이브"),
    DISNEY_PLUS("디즈니+"),
    WATCHA("왓챠"),
    APPLE_TV("애플티비"),
    ;

    private final String type;

    public static PlatformType from(String value) {
        return Arrays.stream(values())
                .filter(p -> p.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.PLATFORM_TYPE_BAD_REQUEST));
    }

    public static PlatformType fromByType(String value) {
        return Arrays.stream(values())
                .filter(p -> p.getType().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.PLATFORM_TYPE_BAD_REQUEST));

    }

    public static List<String> toPlatformTypes(List<String> types) {
        return types.stream()
                .map(type -> Arrays.stream(values())
                        .filter(p -> p.getType().equals(type))
                        .findFirst()
                        .orElseThrow(
                                () -> new RestApiException(EnumErrorCode.PLATFORM_TYPE_BAD_REQUEST)
                        )
                        .name()
                ).toList();
    }

    public static List<String> toKoreanTypes(List<String> englishPlatforms) {
        if (englishPlatforms == null || englishPlatforms.isEmpty()) {
            return List.of();
        }

        return englishPlatforms.stream()
                .map(englishPlatform -> from(englishPlatform).getType())
                .toList();
    }
}
