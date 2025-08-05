package com.example.udtbe.domain.batch.scheduler;

import com.example.udtbe.domain.batch.exception.BatchErrorCode;
import com.example.udtbe.domain.batch.util.TimeUtil;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import com.example.udtbe.global.exception.RestApiException;
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
    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000))
    public void runContentBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(contentBatchJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RestApiException(BatchErrorCode.BATCH_ALREADY_RUNNING);
        } catch (JobRestartException e) {
            throw new RestApiException(BatchErrorCode.BATCH_RESTART_FAILED);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RestApiException(BatchErrorCode.BATCH_ALREADY_COMPLETED);
        } catch (JobParametersInvalidException e) {
            throw new RestApiException(BatchErrorCode.BATCH_INVALID_PARAMETERS);
        }
    }


    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000))
    public void rebuildLuceneIndexWithRetry() {
        luceneIndexService.buildIndexOnStartup();
    }
}
