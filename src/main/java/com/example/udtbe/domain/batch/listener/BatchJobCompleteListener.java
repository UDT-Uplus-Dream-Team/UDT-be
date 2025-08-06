package com.example.udtbe.domain.batch.listener;

import com.example.udtbe.domain.batch.event.BatchJobCompleteEvent;
import com.example.udtbe.domain.batch.scheduler.AdminScheduler;
import com.example.udtbe.domain.content.event.IndexRebuildCompleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobCompleteListener {

    private final ApplicationEventPublisher eventPublisher;
    private final AdminScheduler adminScheduler;

    @EventListener
    @Async
    public void handleBatchComplete(BatchJobCompleteEvent event) {
        log.info("배치 완료 이벤트 수신: {} (상태: {}, 시간: {})",
                event.getJobName(), event.getStatus(), event.getCompletedAt());

        if (event.isDailyBatch() && event.isSuccessful()) {
            log.info("새벽 배치 작업 완료 - 루씬 인덱스 리빌드 시작");
            performLuceneRebuild(event);
        } else {
            log.debug("루씬 리빌드 조건 불충족 - 새벽배치: {}, 성공: {}",
                    event.isDailyBatch(), event.isSuccessful());
        }
    }

    private void performLuceneRebuild(BatchJobCompleteEvent batchEvent) {
        try {
            long startTime = System.currentTimeMillis();

            adminScheduler.rebuildLuceneIndexWithRetry();

            long buildTime = System.currentTimeMillis() - startTime;
            int indexedCount = batchEvent.getTotalProcessedCount();

            IndexRebuildCompleteEvent rebuildEvent = IndexRebuildCompleteEvent.of(
                    this, indexedCount, buildTime);

            eventPublisher.publishEvent(rebuildEvent);

            log.info("루씬 인덱스 리빌드 완료 - 인덱싱: {} 건, 소요시간: {}ms",
                    indexedCount, buildTime);

        } catch (Exception e) {
            log.error("❌ 루씬 인덱스 리빌드 실패: {}", e.getMessage(), e);
        }
    }
}