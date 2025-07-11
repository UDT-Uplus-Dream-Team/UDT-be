package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import java.util.List;

public record FeedbackGetBulkResponse(
        List<FeedbackContentDTO> contents,
        Long nextCursor,
        Boolean hasNext
) {

}
