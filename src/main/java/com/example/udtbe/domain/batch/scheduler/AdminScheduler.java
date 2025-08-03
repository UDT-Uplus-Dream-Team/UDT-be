package com.example.udtbe.domain.batch.scheduler;

import com.example.udtbe.domain.batch.util.TimeUtil;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminScheduler {

    private final JobLauncher jobLauncher;
    private final Job contentBatchJob;
    private final LuceneIndexService luceneIndexService;

    @Scheduled(cron = TimeUtil.SCHEDULED_AT)
    public void runContentBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(contentBatchJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            //todo
        } catch (JobExecutionAlreadyRunningException e) {
            //todo
        } catch (JobParametersInvalidException e) {
            //todo
        } catch (JobRestartException e) {
            //todo
        }
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void rebuildLuceneIndex() {
        log.info("===== 새벽 6시 Lucene 인덱스 리빌드 시작 =====");
        try {
            rebuildLuceneIndexWithRetry();
            log.info("===== 새벽 6시 Lucene 인덱스 리빌드 완료 =====");
        } catch (Exception e) {
            log.error("Lucene 인덱스 리빌드 최종 실패", e);
        }
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000))
    public void rebuildLuceneIndexWithRetry() {
        luceneIndexService.buildIndexOnStartup();
    }

    @Recover
    public void recoverRebuildLuceneIndex(Exception ex) {
        //TODO: 재시도 절차마저 실패시 전략 세우기
        log.error("Lucene 인덱스 리빌드 모든 재시도 실패 - 관리자 확인 필요", ex);
    }
}
