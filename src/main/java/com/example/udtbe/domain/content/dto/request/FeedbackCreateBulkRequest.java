package com.example.udtbe.domain.content.dto.request;

import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import jakarta.validation.Valid;
import java.util.List;

public record FeedbackCreateBulkRequest(
        @Valid
        List<FeedbackCreateDTO> feedbacks
) {

}
