package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import java.time.LocalDateTime;

public class BatchJobMetricFixture {

    public static BatchJobMetric completedJob(Long id, BatchJobType batchJobType,
            long totalRead) {

        BatchJobMetric batchJobMetric = BatchJobMetric.of(batchJobType, BatchJobStatus.NOOP,
                LocalDateTime.now(), LocalDateTime.now());

        batchJobMetric.update(BatchJobStatus.COMPLETED, totalRead, totalRead, 0, 0,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return batchJobMetric;
    }

    public static BatchJobMetric failedJob(Long id, BatchJobType batchJobType,
            long totalRead) {

        BatchJobMetric batchJobMetric = BatchJobMetric.of(batchJobType, BatchJobStatus.NOOP,
                LocalDateTime.now(), LocalDateTime.now());

        batchJobMetric.update(BatchJobStatus.COMPLETED, totalRead, 0, 0, totalRead,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return batchJobMetric;
    }

    public static BatchJobMetric partialCompetedJob(Long id, BatchJobType batchJobType,
            long totalRead, long totalFailed) {
        BatchJobMetric batchJobMetric = BatchJobMetric.of(batchJobType, BatchJobStatus.NOOP,
                LocalDateTime.now(), LocalDateTime.now());

        batchJobMetric.update(BatchJobStatus.PARTIAL_COMPLETED, totalRead, totalRead - totalFailed,
                0, totalFailed, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return batchJobMetric;
    }
}
