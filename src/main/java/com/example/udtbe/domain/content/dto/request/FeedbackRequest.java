package com.example.udtbe.domain.content.dto.request;

public record FeedbackRequest(
        Long contentId,
        boolean feedback
) {

}
