package com.example.udtbe.domain.content.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedbackSortType {

    NEWEST("최신순"),
    OLDEST("과거순"),
    ;

    private final String type;

}
