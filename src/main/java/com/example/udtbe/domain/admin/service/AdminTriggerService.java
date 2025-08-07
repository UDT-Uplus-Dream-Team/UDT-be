package com.example.udtbe.domain.admin.service;

import com.example.udtbe.domain.batch.exception.BatchErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminTriggerService {

    private final JobLauncher jobLauncher;
    private final Job contentBatchJob;

    public void retryFailedBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("type", "failed")
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

}
