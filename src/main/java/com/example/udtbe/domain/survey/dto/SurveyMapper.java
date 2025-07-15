package com.example.udtbe.domain.survey.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.entity.Survey;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)

public class SurveyMapper {

    public static Survey toEntity(SurveyCreateRequest request, Member member) {
        // TODO : 2차 MVP Contents 변경
        return Survey.of(
                PlatformType.toPlatformTypes(request.platforms()),
                GenreType.toGenreTypes(request.genres()),
                List.of(""),
                false,
                member
        );
    }
}
