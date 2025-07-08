package com.example.udtbe.domain.content.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenreType {

    ACTION("액션"),
    FANTASY("판타지"),
    SF("SF"),
    THRILLER("스릴러"),
    MYSTERY("미스터리"),
    ADVENTURE("어드벤처"),
    MUSICAL("뮤지컬"),
    COMEDY("코미디"),
    WESTERN("서부극"),
    ROMANCE("멜로/로맨스"),
    DRAMA("서사/드라마"),
    ANIMATION("애니메이션"),
    HORROR("공포(호러),"),
    DOCUMENTARY("다큐멘터리"),
    CRIME("범죄"),
    MARTIAL_ARTS("무협"),
    HISTORICAL_DRAMA("사극/시대극"),
    ADULT("성인"),
    KIDS("키즈"),
    VARIETY("버라이어티"),
    TALK_SHOW("토크쇼"),
    SURVIVAL("서바이벌"),
    REALITY("리얼리티"),
    STAND_UP_COMEDY("스탠드업코미디"),
    ;

    private final String type;

    public static GenreType from(String value) {
        return Arrays.stream(values())
                .filter(g -> g.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.GENRE_TYPE_NOT_FOUND));
    }

    public static List<String> toGenreTypes(List<String> types) {
        return types.stream()
                .map(type -> Arrays.stream(values())
                        .filter(g -> g.getType().equals(type))
                        .findFirst()
                        .orElseThrow(
                                () -> new RestApiException(EnumErrorCode.GENRE_TYPE_NOT_FOUND)
                        )
                        .name()
                ).toList();
    }
}
