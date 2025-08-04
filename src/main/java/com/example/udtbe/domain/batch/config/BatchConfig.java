package com.example.udtbe.domain.batch.config;


import com.example.udtbe.domain.admin.dto.AdminContentMapper;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.service.AdminQuery;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.lisener.StepStatsListener;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(BatchProperties.class)
@RequiredArgsConstructor
public class BatchConfig {

    private static final String CONTENT_BATCH_JOB = "contentBatchJob";
    public static final String REGISTER_STEP = "contentRegisterStep";
    public static final String UPDATE_STEP = "contentUpdateStep";
    public static final String DELETE_STEP = "contentDeleteStep";
    public static final String FEEDBACK_STEP = "feedbackStep";
    private static final int CHUNK_SIZE = 5;
    private static final int RETRY_LIMIT = 3;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AdminService adminService;
    private final AdminQuery adminQuery;
    private final AdminContentUpdateJobRepository adminContentUpdateJobRepository;
    private final AdminContentRegisterJobRepository adminContentRegisterJobRepository;
    private final AdminContentDeleteJobRepository adminContentDeleteJobRepository;
    private final StepStatsListener stepStatsListener;


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher,
            JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher,
                jobExplorer, jobRepository);

        String jobNames = properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }
        return runner;
    }


    @Bean
    public Job contentBatchJob() {
        return new JobBuilder(CONTENT_BATCH_JOB, jobRepository)
                .start(contentRegisterStep())
                .next(contentUpdateStep())
                .next(contentDeleteStep())
                .build();
    }

    @Bean
    @JobScope
    public Step contentRegisterStep() {
        return new StepBuilder(REGISTER_STEP, jobRepository)
                .<AdminContentRegisterJob, AdminContentRegisterJob>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(contentRegisterReader())
                .processor(contentRegisterProcessor())
                .faultTolerant()
                .retryLimit(RETRY_LIMIT)
                .retry(Exception.class)
                .writer(contentRegisterWriter())
                .listener(stepStatsListener)
                .build();
    }

    @Bean
    @JobScope
    public Step contentUpdateStep() {
        return new StepBuilder(UPDATE_STEP, jobRepository)
                .<AdminContentUpdateJob, AdminContentUpdateJob>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(contentUpdateReader())
                .processor(contentUpdateProcessor())
                .writer(contentUpdateWriter())
                .faultTolerant()
                .retryLimit(RETRY_LIMIT)
                .retry(Exception.class)
                .listener(stepStatsListener)
                .build();
    }

    @Bean
    @JobScope
    public Step contentDeleteStep() {
        return new StepBuilder(DELETE_STEP, jobRepository)
                .<AdminContentDeleteJob, AdminContentDeleteJob>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(contentDeleteReader())
                .processor(contentDeleteProcessor())
                .writer(contentDeleteWriter())
                .faultTolerant()
                .retryLimit(RETRY_LIMIT)
                .retry(Exception.class)
                .listener(stepStatsListener)
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<AdminContentRegisterJob> contentRegisterReader() {
        List<AdminContentRegisterJob> pendingJobs = adminContentRegisterJobRepository.findByStatus(
                BatchStatus.PENDING);
        return new ListItemReader<>(pendingJobs);
    }

    @Bean
    @StepScope
    public ListItemReader<AdminContentUpdateJob> contentUpdateReader() {
        List<AdminContentUpdateJob> pendingJobs = adminContentUpdateJobRepository.findByStatus(
                BatchStatus.PENDING);
        return new ListItemReader<>(pendingJobs);
    }

    @Bean
    @StepScope
    public ListItemReader<AdminContentDeleteJob> contentDeleteReader() {
        List<AdminContentDeleteJob> pendingJobs = adminContentDeleteJobRepository.findByStatus(
                BatchStatus.PENDING);
        return new ListItemReader<>(pendingJobs);
    }

    @Bean
    @StepScope
    public ItemProcessor<AdminContentRegisterJob, AdminContentRegisterJob> contentRegisterProcessor() {
        return request -> {
            request.changeStatus(BatchStatus.PROCESSING);
            return request;
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<AdminContentUpdateJob, AdminContentUpdateJob> contentUpdateProcessor() {
        return request -> {
            request.changeStatus(BatchStatus.PROCESSING);
            return request;
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<AdminContentDeleteJob, AdminContentDeleteJob> contentDeleteProcessor() {
        return request -> {
            request.changeStatus(BatchStatus.PROCESSING);
            return request;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AdminContentRegisterJob> contentRegisterWriter() {
        return items -> {
            items.forEach(item -> {

                try {
                    AdminContentRegisterRequest request = AdminContentMapper.toContentRegisterRequest(
                            item);
                    adminQuery.validRegisterAndUpdateContent(request.categories(),
                            request.platforms(), request.casts(), request.directors());
                    adminService.registerContent(request);
                    item.changeStatus(BatchStatus.COMPLETED);
                    item.finish();
                    adminContentRegisterJobRepository.save(item);
                } catch (Exception e) {
                    item.changeStatus(BatchStatus.FAILED);
                    item.finish();
                    adminContentRegisterJobRepository.save(item);
                }
            });
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AdminContentUpdateJob> contentUpdateWriter() {
        return items -> {
            items.forEach(item -> {
                try {
                    AdminContentUpdateRequest request = AdminContentMapper.toContentUpdateRequest(
                            item);
                    adminQuery.validContentByContentId(item.getContentId());
                    adminQuery.validRegisterAndUpdateContent(request.categories(),
                            request.platforms(), request.casts(), request.directors());
                    adminService.updateContent(item.getContentId(), request);
                    item.changeStatus(BatchStatus.COMPLETED);
                    item.finish();
                    adminContentUpdateJobRepository.save(item);
                } catch (Exception e) {
                    item.changeStatus(BatchStatus.FAILED);
                    item.finish();
                    adminContentUpdateJobRepository.save(item);
                }
            });
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AdminContentDeleteJob> contentDeleteWriter() {
        return items -> {
            items.forEach(item -> {
                try {
                    adminQuery.validContentByContentId(item.getContentId());
                    adminService.deleteContent(item.getContentId());
                    item.changeStatus(BatchStatus.COMPLETED);
                    item.finish();
                    adminContentDeleteJobRepository.save(item);
                } catch (Exception e) {
                    item.changeStatus(BatchStatus.FAILED);
                    item.finish();
                    adminContentDeleteJobRepository.save(item);
                }
            });
        };
    }
}

