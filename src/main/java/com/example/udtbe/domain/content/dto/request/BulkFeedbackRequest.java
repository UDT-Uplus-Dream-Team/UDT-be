package com.example.udtbe.domain.content.dto.request;

import java.util.List;

public record BulkFeedbackRequest(
        List<FeedbackRequest> feedbacks
) {

}
