package com.example.udtbe.domain.batch.lisener;

import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.config.BatchConfig;
import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StepStatsListener implements StepExecutionListener {

    private final AdminService adminService;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("===== Step 시작: {} =====", stepExecution.getStepName());
        log.info("Job ID: {}, Job Instance ID: {}",
                stepExecution.getJobExecutionId(),
                stepExecution.getJobExecution().getJobInstance().getId());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        BatchJobType batchJobType = determineBatchJobType(stepExecution.getStepName());

        long totalRead = stepExecution.getReadCount();
        long totalWrite = stepExecution.getWriteCount();
        long totalSkip = stepExecution.getSkipCount();
        long processCount = stepExecution.getProcessSkipCount();
        long readSkip = stepExecution.getReadSkipCount();
        long writeSkip = stepExecution.getWriteSkipCount();
        long rollbackCount = stepExecution.getRollbackCount();
        long commitCount = stepExecution.getCommitCount();

        // 상세 통계 로깅
        logDetailedStats(stepExecution, totalRead, totalWrite, totalSkip,
                processCount, readSkip, writeSkip, rollbackCount, commitCount);

        BatchJobStatus batchJobStatus = determineBatchJobStatus(totalRead, totalWrite, totalSkip);

        // 에러 정보 로깅
        logFailureExceptions(stepExecution);

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

        log.info("===== Step 완료: {} =====", stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }

    private BatchJobType determineBatchJobType(String stepName) {
        if (stepName.equals(BatchConfig.REGISTER_STEP)) {
            return BatchJobType.REGISTER;
        } else if (stepName.equals(BatchConfig.UPDATE_STEP)) {
            return BatchJobType.UPDATE;
        } else if (stepName.equals(BatchConfig.DELETE_STEP)) {
            return BatchJobType.DELETE;
        }
        return BatchJobType.REGISTER; // 기본값
    }

    private BatchJobStatus determineBatchJobStatus(long totalRead, long totalWrite,
            long totalSkip) {
        if (totalRead == 0) {
            return BatchJobStatus.NOOP;
        } else if (totalWrite < 0 || totalSkip > 0) {
            return BatchJobStatus.PARTIAL_COMPLETED;
        } else if (totalSkip == 0) {
            return BatchJobStatus.COMPLETED;
        } else if (totalWrite == 0) {
            return BatchJobStatus.FAILED;
        }
        return BatchJobStatus.PARTIAL_COMPLETED; // 기본값
    }

    private void logDetailedStats(StepExecution stepExecution, long totalRead, long totalWrite,
            long totalSkip, long processCount, long readSkip, long writeSkip,
            long rollbackCount, long commitCount) {

        log.info(" Step 실행 통계 - {}", stepExecution.getStepName());
        log.info("  ├─ 읽기: {} 건", totalRead);
        log.info("  ├─ 쓰기: {} 건", totalWrite);
        log.info("  ├─ 전체 스킵: {} 건", totalSkip);
        log.info("  ├─ 처리 스킵: {} 건", processCount);
        log.info("  ├─ 읽기 스킵: {} 건", readSkip);
        log.info("  ├─ 쓰기 스킵: {} 건", writeSkip);
        log.info("  ├─ 롤백: {} 회", rollbackCount);
        log.info("  ├─ 커밋: {} 회", commitCount);
        long executionTime =
                stepExecution.getEndTime() != null && stepExecution.getStartTime() != null
                        ? ChronoUnit.MILLIS.between(stepExecution.getStartTime(),
                        stepExecution.getEndTime())
                        : 0L;
        log.info("  ├─ 실행 시간: {}ms", executionTime);
        log.info("  └─ 종료 상태: {}", stepExecution.getExitStatus().getExitCode());

        // 성공률 계산
        if (totalRead > 0) {
            double successRate = ((double) totalWrite / totalRead) * 100;
            double skipRate = ((double) totalSkip / totalRead) * 100;
            log.info("배치 성공률: {}%, 스킵률: {}%", String.format("%.2f", successRate),
                    String.format("%.2f", skipRate));
        }
    }

    private void logFailureExceptions(StepExecution stepExecution) {
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            log.error("!!Step 실행 중 발생한 예외들!!:");
            stepExecution.getFailureExceptions().forEach(ex ->
                    log.error("  └─ {}: {}", ex.getClass().getSimpleName(), ex.getMessage())
            );
        }
    }
}
