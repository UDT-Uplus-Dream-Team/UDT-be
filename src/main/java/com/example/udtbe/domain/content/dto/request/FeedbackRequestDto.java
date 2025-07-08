package com.example.udtbe.domain.content.dto.request;

public record FeedbackRequestDto(
        Long contentId,
        boolean feedback
) {

}
