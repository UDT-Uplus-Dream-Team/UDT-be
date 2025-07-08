package com.example.udtbe.domain.content.dto.response;

import java.util.List;

public record BulkFeedbackResponseDto(
        List<FeedbackResponseDto> feedbacks,
        String nextCursor,
        Boolean hasNext
) {

}
