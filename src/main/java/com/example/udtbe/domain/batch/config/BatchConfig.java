package com.example.udtbe.domain.batch.config;


import com.example.udtbe.domain.admin.dto.AdminContentMapper;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final AdminService adminService;
    private final AdminContentUpdateJobRepository adminContentUpdateJobRepository;
    private final AdminContentRegisterJobRepository adminContentRegisterJobRepository;
    private final AdminContentDeleteJobRepository adminContentDeleteJobRepository;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job contentBatchJob() {
        return new JobBuilder("contentBatchJob", jobRepository)
                .start(contentRegisterStep())
                .next(contentUpdateStep())
                .next(contentDeleteStep())
                .build();
    }

    @Bean
    @JobScope
    public Step contentRegisterStep() {
        return new StepBuilder("contentRegisterStep", jobRepository)
                .<AdminContentRegisterJob, AdminContentRegisterJob>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(contentRegisterReader())
                .processor(contentRegisterProcessor())
                .writer(contentRegisterWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step contentUpdateStep() {
        return new StepBuilder("contentUpdateStep", jobRepository)
                .<AdminContentUpdateJob, AdminContentUpdateJob>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(contentUpdateReader())
                .processor(contentUpdateProcessor())
                .writer(contentUpdateWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step contentDeleteStep() {
        return new StepBuilder("contentDeleteStep", jobRepository)
                .<AdminContentDeleteJob, AdminContentDeleteJob>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(contentDeleteReader())
                .processor(contentDeleteProcessor())
                .writer(contentDeleteWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<AdminContentRegisterJob> contentRegisterReader() {
        return new JpaPagingItemReaderBuilder<AdminContentRegisterJob>()
                .name("contentRegisterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT r FROM AdminContentRegisterJob r WHERE r.status = :status")
                .parameterValues(Map.of("status", BatchStatus.PENDING))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<AdminContentUpdateJob> contentUpdateReader() {
        return new JpaPagingItemReaderBuilder<AdminContentUpdateJob>()
                .name("contentUpdateReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT c FROM AdminContentUpdateJob c WHERE c.status = :status")
                .parameterValues(Map.of("status", BatchStatus.PENDING))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<AdminContentDeleteJob> contentDeleteReader() {
        return new JpaPagingItemReaderBuilder<AdminContentDeleteJob>()
                .name("contentDeleteReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT c FROM AdminContentDeleteJob c WHERE c.status = :status")
                .parameterValues(Map.of("status", BatchStatus.PENDING))
                .build();
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
                    AdminContentRegisterRequest adminContentRegisterRequest = AdminContentMapper.toContentRegisterRequest(
                            item);
                    adminService.registerContent(adminContentRegisterRequest);
                    item.changeStatus(BatchStatus.COMPLETED);
                    adminContentRegisterJobRepository.save(item);
                } catch (Exception e) {
                    item.changeStatus(BatchStatus.FAILED);
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
                    AdminContentUpdateRequest adminContentUpdateRequest = AdminContentMapper.toContentUpdateRequest(
                            item);
                    adminService.updateContent(item.getContentId(), adminContentUpdateRequest);
                    item.changeStatus(BatchStatus.COMPLETED);
                    adminContentUpdateJobRepository.save(item);
                } catch (Exception e) {
                    item.changeStatus(BatchStatus.FAILED);
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
                    adminService.deleteContent(item.getContentId());
                    item.changeStatus(BatchStatus.COMPLETED);
                    adminContentDeleteJobRepository.save(item);
                } catch (Exception e) {
                    item.changeStatus(BatchStatus.FAILED);
                    adminContentDeleteJobRepository.save(item);
                }
            });
        };
    }
}

