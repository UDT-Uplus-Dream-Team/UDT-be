package com.example.udtbe.domain.batch.lisener;

import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.config.BatchConfig;
import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StepStatsListener implements StepExecutionListener {

    private final AdminService adminService;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        BatchJobType batchJobType = BatchJobType.REGISTER;
        BatchJobStatus batchJobStatus = BatchJobStatus.NOOP;

        if (stepExecution.getStepName().equals(BatchConfig.REGISTER_STEP)) {
            batchJobType = BatchJobType.REGISTER;
        } else if (stepExecution.getStepName().equals(BatchConfig.UPDATE_STEP)) {
            batchJobType = BatchJobType.UPDATE;
        } else if (stepExecution.getStepName().equals(BatchConfig.DELETE_STEP)) {
            batchJobType = BatchJobType.DELETE;
        } else if (stepExecution.getStepName().equals(BatchConfig.FEEDBACK_STEP)) {
            batchJobType = BatchJobType.FEEDBACK;
        }

        long totalRead = stepExecution.getReadCount();
        long totalWrite = stepExecution.getWriteCount();
        long totalSkip = stepExecution.getSkipCount();

        if (totalRead == 0) {
            batchJobStatus = BatchJobStatus.NOOP;
        } else if (totalWrite < 0 || totalSkip > 0) {
            batchJobStatus = BatchJobStatus.PARTIAL_COMPLETED;
        } else if (totalSkip == 0) {
            batchJobStatus = BatchJobStatus.COMPLETED;
        } else if (totalWrite == 0) {
            batchJobStatus = BatchJobStatus.FAILED;
        }

        BatchJobMetric metric = BatchJobMetric.of(
                stepExecution.getJobExecutionId(),
                batchJobType,
                batchJobStatus,
                totalRead,
                totalWrite,
                totalSkip,
                stepExecution.getStartTime(),
                stepExecution.getEndTime()
        );

        adminService.updateMetric(metric);
        return stepExecution.getExitStatus();
    }
}
