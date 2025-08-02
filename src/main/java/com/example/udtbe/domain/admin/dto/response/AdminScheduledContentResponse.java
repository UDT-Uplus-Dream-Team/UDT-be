package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.domain.batch.entity.enums.BatchStepStatus;
import java.time.LocalDateTime;

public record AdminScheduledContentResponse(
        Long id,
        BatchStepStatus status,
        Long memberId,
        LocalDateTime createdAt,
        LocalDateTime updateAt,
        LocalDateTime finishedAt,
        BatchJobType jobType
) {

}
