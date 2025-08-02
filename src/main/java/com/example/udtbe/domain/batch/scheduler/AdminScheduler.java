package com.example.udtbe.domain.batch.scheduler;

import com.example.udtbe.domain.batch.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminScheduler {

    private final JobLauncher jobLauncher;
    private final Job contentBatchJob;

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
}
