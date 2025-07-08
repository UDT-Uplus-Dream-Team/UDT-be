package com.example.udtbe.domain.content.dto.response;

import java.util.List;

public record FeedbackBulkResponse(
        List<FeedbackResponse> feedbacks,
        String nextCursor,
        Boolean hasNext
) {

}
