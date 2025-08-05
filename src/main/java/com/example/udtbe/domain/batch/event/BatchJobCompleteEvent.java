package com.example.udtbe.domain.batch.event;

import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BatchJobCompleteEvent extends ApplicationEvent {

    private final String jobName;
    private final BatchJobStatus status;
    private final LocalDateTime completedAt;
    private final long executionTimeMs;
    private final int totalProcessedCount;

    public BatchJobCompleteEvent(Object source, String jobName, BatchJobStatus status,
            LocalDateTime completedAt, long executionTimeMs, int totalProcessedCount) {
        super(source);
        this.jobName = jobName;
        this.status = status;
        this.completedAt = completedAt;
        this.executionTimeMs = executionTimeMs;
        this.totalProcessedCount = totalProcessedCount;
    }

    public static BatchJobCompleteEvent of(Object source, String jobName, BatchJobStatus status,
            LocalDateTime completedAt, long executionTimeMs, int totalProcessedCount) {
        return new BatchJobCompleteEvent(source, jobName, status, completedAt, executionTimeMs,
                totalProcessedCount);
    }

    public boolean isDailyBatch() {
        return completedAt.getHour() >= 4 && completedAt.getHour() < 6;
    }

    public boolean isSuccessful() {
        return status == BatchJobStatus.COMPLETED || status == BatchJobStatus.PARTIAL_COMPLETED;
    }
}