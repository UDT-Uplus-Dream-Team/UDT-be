package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import java.util.Collections;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class SurveyFixture {

    public static Survey survey(List<String> platformTypes, List<String> genreTypes,
            Member member) {
        return Survey.of(
                platformTypes,
                genreTypes,
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey survey(List<String> contents, Member member) {
        return Survey.of(
                List.of("TVING", "WAVVE"),
                List.of("CRIME", "THRILLER"),
                contents,
                false,
                member
        );
    }

    // === 특정 장르 선호 설문 메서드들 ===

    public static Survey actionThrillerSurvey(Member member) {
        return Survey.of(
                List.of("NETFLIX", "WATCHA"),
                List.of("ACTION", "THRILLER"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey scienceFictionSurvey(Member member) {
        return Survey.of(
                List.of("DISNEY_PLUS", "NETFLIX"),
                List.of("SF", "FANTASY"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey netflixOnlySurvey(Member member) {
        return Survey.of(
                List.of("NETFLIX"),
                List.of("ACTION", "DRAMA"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey disneyPlusSurvey(Member member) {
        return Survey.of(
                List.of("DISNEY_PLUS"),
                List.of("ACTION", "ADVENTURE"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey musicalRomanceSurvey(Member member) {
        return Survey.of(
                List.of("NETFLIX", "WATCHA", "TVING"),
                List.of("MUSICAL", "ROMANCE"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey horrorThrillerSurvey(Member member) {
        return Survey.of(
                List.of("NETFLIX", "WATCHA"),
                List.of("HORROR", "THRILLER", "MYSTERY"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey familyFriendlySurvey(Member member) {
        return Survey.of(
                List.of("DISNEY_PLUS", "NETFLIX"),
                List.of("ANIMATION", "ADVENTURE", "FAMILY"),
                Collections.emptyList(),
                false,
                member
        );
    }

    public static Survey emptyPlatformSurvey(Member member) {
        return Survey.of(
                List.of(), // 빈 플랫폼 리스트
                List.of("ACTION"),
                Collections.emptyList(),
                false,
                member
        );
    }
}
