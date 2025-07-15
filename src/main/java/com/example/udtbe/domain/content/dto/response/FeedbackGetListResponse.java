package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import java.util.List;

public record FeedbackGetListResponse(
        List<FeedbackContentDTO> contents,
        Long nextCursor,
        Boolean hasNext
) {

}
