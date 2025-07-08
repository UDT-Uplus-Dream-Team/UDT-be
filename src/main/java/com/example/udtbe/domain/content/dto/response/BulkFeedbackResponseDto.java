package com.example.udtbe.domain.content.dto.response;

import java.util.List;

public record BulkFeedbackResponseDto(
        List<FeedbackResponseDto> feedbacks,
        Long nextCursor,
        Boolean hasNext
) {

}
