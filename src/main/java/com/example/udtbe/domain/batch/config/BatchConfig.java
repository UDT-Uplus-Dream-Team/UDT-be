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
import com.example.udtbe.domain.batch.lisener.BatchSkipListener;
import com.example.udtbe.domain.batch.lisener.StepStatsListener;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {

    private static final String CONTENT_BATCH_JOB = "contentBatchJob";
    private static final String INVALID_REGISTER = "유효하지 않은 등록";
    private static final String INVALID_UPDATE = "유효하지 않은 수정";
    private static final String INVALID_DELETE = "유효하지 않은 삭제";


    public static final String REGISTER_STEP = "contentRegisterStep";
    public static final String UPDATE_STEP = "contentUpdateStep";
    public static final String DELETE_STEP = "contentDeleteStep";

    private static final int CHUNK_SIZE = 10;
    private static final int RETRY_LIMIT = 3;
    private static final int SKIP_LIMIT = 200;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AdminService adminService;
    private final AdminQuery adminQuery;
    private final AdminContentUpdateJobRepository adminContentUpdateJobRepository;
    private final AdminContentRegisterJobRepository adminContentRegisterJobRepository;
    private final AdminContentDeleteJobRepository adminContentDeleteJobRepository;
    private final StepStatsListener stepStatsListener;
    private final BatchSkipListener batchSkipListener;

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
                .writer(contentRegisterWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(SKIP_LIMIT)
                .retry(Exception.class)
                .retryLimit(RETRY_LIMIT)
                .listener(stepStatsListener)
                .listener(batchSkipListener)
                .build();
    }

    @Bean
    @JobScope
    public Step contentUpdateStep() {
        return new StepBuilder(UPDATE_STEP, jobRepository)
                .<AdminContentUpdateJob, AdminContentUpdateJob>chunk(CHUNK_SIZE, transactionManager)
                .reader(contentUpdateReader())
                .processor(contentUpdateProcessor())
                .writer(contentUpdateWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(SKIP_LIMIT)
                .retry(Exception.class)
                .retryLimit(RETRY_LIMIT)
                .listener(stepStatsListener)
                .listener(batchSkipListener)
                .build();
    }

    @Bean
    @JobScope
    public Step contentDeleteStep() {
        return new StepBuilder(DELETE_STEP, jobRepository)
                .<AdminContentDeleteJob, AdminContentDeleteJob>chunk(CHUNK_SIZE, transactionManager)
                .reader(contentDeleteReader())
                .processor(contentDeleteProcessor())
                .writer(contentDeleteWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(SKIP_LIMIT)
                .retry(Exception.class)
                .retryLimit(RETRY_LIMIT)
                .listener(stepStatsListener)
                .listener(batchSkipListener)
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
        return item -> item;
    }

    @Bean
    @StepScope
    public ItemProcessor<AdminContentUpdateJob, AdminContentUpdateJob> contentUpdateProcessor() {
        return item -> item;
    }

    @Bean
    @StepScope
    public ItemProcessor<AdminContentDeleteJob, AdminContentDeleteJob> contentDeleteProcessor() {
        return item -> item;
    }

    @Bean
    @StepScope
    public ItemWriter<AdminContentRegisterJob> contentRegisterWriter() {
        return items -> {
            for (AdminContentRegisterJob item : items) {
                item.changeStatus(BatchStatus.PROCESSING);
                adminContentRegisterJobRepository.save(item);
                AdminContentRegisterRequest request = AdminContentMapper.toContentRegisterRequest(
                        item);
                if (item.getTitle().contains("스킵테스트")) {
                    throw new Exception("안녕");
                }
                try {
                    adminQuery.validRegisterAndUpdateContent(request.categories(),
                            request.platforms(),
                            request.casts(), request.directors());
                    adminService.registerContent(request);
                    item.changeStatus(BatchStatus.COMPLETED);
                    item.finish();
                    adminContentRegisterJobRepository.save(item);
                } catch (RestApiException e) {
                    item.changeStatus(BatchStatus.INVALID);
                    item.setError(INVALID_REGISTER, e.getMessage());
                    adminContentRegisterJobRepository.save(item);
                }
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AdminContentUpdateJob> contentUpdateWriter() {
        return items -> {
            for (AdminContentUpdateJob item : items) {
                item.changeStatus(BatchStatus.PROCESSING);
                adminContentUpdateJobRepository.save(item);
                AdminContentUpdateRequest request = AdminContentMapper.toContentUpdateRequest(
                        item);

                try {
                    adminQuery.validContentByContentId(item.getContentId());
                    adminQuery.validRegisterAndUpdateContent(request.categories(),
                            request.platforms(),
                            request.casts(), request.directors());
                    adminService.updateContent(item.getContentId(), request);
                    item.changeStatus(BatchStatus.COMPLETED);
                    item.finish();
                    adminContentUpdateJobRepository.save(item);
                } catch (RestApiException e) {
                    item.changeStatus(BatchStatus.INVALID);
                    item.setError(INVALID_UPDATE, e.getMessage());
                    adminContentUpdateJobRepository.save(item);
                    break;
                }
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AdminContentDeleteJob> contentDeleteWriter() {
        return items -> {
            for (AdminContentDeleteJob item : items) {
                item.changeStatus(BatchStatus.PROCESSING);
                adminContentDeleteJobRepository.save(item);

                try {
                    adminQuery.validContentByContentId(item.getContentId());
                    adminService.deleteContent(item.getContentId());
                    item.changeStatus(BatchStatus.COMPLETED);
                    item.finish();
                    adminContentDeleteJobRepository.save(item);
                } catch (RestApiException e) {
                    item.changeStatus(BatchStatus.INVALID);
                    item.setError(INVALID_DELETE, e.getMessage());
                    adminContentDeleteJobRepository.save(item);
                }
            }
        };
    }
}
