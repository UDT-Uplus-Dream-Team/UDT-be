package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import java.time.LocalDateTime;

public class BatchJobMetricFixture {

    public static BatchJobMetric completedJob(Long id, BatchJobType batchJobType,
            long totalRead) {
        return BatchJobMetric.of(id, batchJobType,
                BatchJobStatus.COMPLETED, totalRead, totalRead, 0, LocalDateTime.now(),
                LocalDateTime.now().plusHours(1));
    }

    public static BatchJobMetric failedJob(Long id, BatchJobType batchJobType,
            long totalRead) {
        return BatchJobMetric.of(id, batchJobType,
                BatchJobStatus.COMPLETED, totalRead, 0, totalRead, LocalDateTime.now(),
                LocalDateTime.now().plusHours(1));
    }

    public static BatchJobMetric partialCompetedJob(Long id, BatchJobType batchJobType,
            long totalRead, long totalFailed) {
        return BatchJobMetric.of(id, batchJobType,
                BatchJobStatus.PARTIAL_COMPLETED, totalRead, totalRead - totalFailed, totalFailed,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    }
}
