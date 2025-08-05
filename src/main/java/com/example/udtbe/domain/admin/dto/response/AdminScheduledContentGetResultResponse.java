package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import java.time.LocalDateTime;

public record AdminScheduledContentGetResultResponse(

        Long resultId,
        BatchJobType type,
        BatchJobStatus status,
        long totalRead,
        long totalCompleted,
        long totalInvalid,
        long totalFailed,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

}
