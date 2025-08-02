package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import java.time.LocalDateTime;

public record AdminScheduledContentResultResponse(

        Long resultId,
        BatchJobType type,
        BatchJobStatus status,
        long totalRead,
        long totalWrite,
        long totalSkip,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

}
