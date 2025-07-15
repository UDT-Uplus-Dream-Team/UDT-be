package com.example.udtbe.common.fixture;

import static com.example.udtbe.domain.content.entity.enums.PlatformType.APPLE_TV;
import static com.example.udtbe.domain.content.entity.enums.PlatformType.COUPANG_PLAY;
import static com.example.udtbe.domain.content.entity.enums.PlatformType.DISNEY_PLUS;
import static com.example.udtbe.domain.content.entity.enums.PlatformType.NETFLIX;
import static com.example.udtbe.domain.content.entity.enums.PlatformType.TVING;
import static com.example.udtbe.domain.content.entity.enums.PlatformType.WATCHA;
import static com.example.udtbe.domain.content.entity.enums.PlatformType.WAVVE;
import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class PlatformFixture {

    public static List<Platform> platforms() {
        List<PlatformType> platformTypes = List.of(
                NETFLIX,
                TVING,
                COUPANG_PLAY,
                WAVVE,
                DISNEY_PLUS,
                WATCHA,
                APPLE_TV
        );

        List<Platform> platforms = new ArrayList<>();
        for (PlatformType platformType : platformTypes) {
            platforms.add(Platform.of(platformType));
        }

        return platforms;
    }

}
