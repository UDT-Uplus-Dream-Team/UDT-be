package com.example.udtbe.domain.batch.listener;

import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.config.BatchConfig;
import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.domain.batch.event.BatchJobCompleteEvent;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StepStatsListener implements StepExecutionListener {

    private final AdminService adminService;
    private final ApplicationEventPublisher eventPublisher;

    // 커스텀 통계 추적
    private int successCount = 0;
    private int skipCount = 0;
    private int retryCount = 0;
    private int failCount = 0;
    private int systemFailureCount = 0;

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

        // 통합 배치 처리 결과 로깅
        logIntegratedStats(stepExecution, totalRead);

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

        if (BatchConfig.DELETE_STEP.equals(stepExecution.getStepName())) {
            publishBatchCompleteEvent(stepExecution, batchJobStatus, totalRead);
        }

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

    private void logFailureExceptions(StepExecution stepExecution) {
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            log.error("!!Step 실행 중 발생한 예외들!!:");
            stepExecution.getFailureExceptions().forEach(ex ->
                    log.error("  └─ {}: {}", ex.getClass().getSimpleName(), ex.getMessage())
            );
        }
    }

    // 통합 배치 처리 결과 로깅
    private void logIntegratedStats(StepExecution stepExecution, long totalRead) {
        long executionTime =
                stepExecution.getEndTime() != null && stepExecution.getStartTime() != null
                        ? ChronoUnit.MILLIS.between(stepExecution.getStartTime(),
                        stepExecution.getEndTime())
                        : 0L;

        int totalProcessed = successCount + skipCount + failCount + systemFailureCount;

        log.info("📈 배치 처리 결과 - {}", stepExecution.getStepName());
        log.info("  ├─ 📖 읽기: {} 건", totalRead);

        if (totalProcessed > 0) {
            log.info("  ├─ ✅ 성공: {} 건", successCount);
            log.info("  ├─ ⏭️ 스킵: {} 건", skipCount);
            log.info("  ├─ ❌ 실패: {} 건", failCount);

            if (systemFailureCount > 0) {
                log.info("  ├─ 💥 시스템장애: {} 건", systemFailureCount);
            }

            if (retryCount > 0) {
                log.info("  ├─ 🔄 재시도: {} 회", retryCount);
            }

            double successRate = ((double) successCount / totalProcessed) * 100;
            log.info("  ├─ 📊 성공률: {}%", String.format("%.2f", successRate));
        } else {
            log.info("  ├─ ℹ️ 처리된 아이템 없음");
        }

        log.info("  ├─ ⏱️ 실행시간: {}ms", executionTime);
        log.info("  └─ 🏁 종료상태: {}", stepExecution.getExitStatus().getExitCode());

        // 통계 초기화
        resetStats();
    }

    // 통계 업데이트 메서드들
    public void incrementSuccess() {
        successCount++;
        log.debug("✅ 성공 카운트 증가: {}", successCount);
    }

    public void incrementSkip() {
        skipCount++;
        log.debug("⏭️ 스킵 카운트 증가: {}", skipCount);
    }

    public void incrementRetry() {
        retryCount++;
        log.debug("🔄 재시도 카운트 증가: {}", retryCount);
    }

    public void incrementFail() {
        failCount++;
        log.debug("❌ 실패 카운트 증가: {}", failCount);
    }

    public void incrementSystemFailure() {
        systemFailureCount++;
        log.warn("💥 시스템 장애 감지! 카운트: {}", systemFailureCount);
    }

    private void resetStats() {
        successCount = 0;
        skipCount = 0;
        retryCount = 0;
        failCount = 0;
        systemFailureCount = 0;
    }

    /**
     * 배치 완료 이벤트 발행
     */
    private void publishBatchCompleteEvent(StepExecution stepExecution,
            BatchJobStatus batchJobStatus, long totalRead) {
        long executionTime =
                stepExecution.getEndTime() != null && stepExecution.getStartTime() != null
                        ? ChronoUnit.MILLIS.between(stepExecution.getStartTime(),
                        stepExecution.getEndTime())
                        : 0L;

        int totalProcessed = successCount + skipCount + failCount + systemFailureCount;

        BatchJobCompleteEvent event = BatchJobCompleteEvent.of(
                this,
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                batchJobStatus,
                LocalDateTime.now(),
                executionTime,
                totalProcessed
        );

        eventPublisher.publishEvent(event);
        log.info("🚀 배치 완료 이벤트 발행: {} (처리: {} 건, 실행시간: {}ms)",
                event.getJobName(), totalProcessed, executionTime);
    }
}
