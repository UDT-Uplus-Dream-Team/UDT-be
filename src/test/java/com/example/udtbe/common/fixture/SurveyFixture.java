package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class SurveyFixture {

    public static Survey survey(List<String> platformTypes, List<String> genreTypes,
            Member member) {
        return Survey.of(
                platformTypes,
                genreTypes,
                List.of(""),
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
}
