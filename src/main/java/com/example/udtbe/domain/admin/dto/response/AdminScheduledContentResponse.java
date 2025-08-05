package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import java.time.LocalDateTime;

public record AdminScheduledContentResponse(
        Long id,
        BatchStatus status,
        Long memberId,
        LocalDateTime createdAt,
        LocalDateTime updateAt,
        LocalDateTime finishedAt,
        BatchJobType jobType
) {

}
