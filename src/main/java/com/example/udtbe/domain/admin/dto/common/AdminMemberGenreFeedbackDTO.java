package com.example.udtbe.domain.admin.dto.common;

import com.example.udtbe.domain.content.entity.enums.GenreType;

public record AdminMemberGenreFeedbackDTO(
        GenreType genreType,
        long likeCount,
        long dislikeCount,
        long uninterestedCount
) {

}
