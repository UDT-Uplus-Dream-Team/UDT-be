package com.example.udtbe.domain.content.dto.request;

import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import java.util.List;

public record FeedbackCreateBulkRequest(
        List<FeedbackCreateDTO> feedbacks
) {

}
