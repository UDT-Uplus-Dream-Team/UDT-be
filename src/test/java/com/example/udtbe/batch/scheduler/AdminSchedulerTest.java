package com.example.udtbe.batch.scheduler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.scheduler.AdminScheduler;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import com.example.udtbe.global.exception.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@EnableRetry
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ContextConfiguration(classes = {AdminScheduler.class})
class AdminSchedulerTest {

    @Mock
    private LuceneIndexService luceneIndexService;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private AdminService adminService;

    @Mock
    private Job contentBatchJob;

    @InjectMocks
    private AdminScheduler adminScheduler;

    @Test
    @DisplayName("성공적으로 잡을 실행한다")
    void launchesTheJobSuccessfully() throws Exception {
        // given
        given(jobLauncher.run(any(Job.class), any(JobParameters.class)))
                .willReturn(new JobExecution(1L));
        // when
        adminScheduler.runContentBatchJob();

        // then
        verify(jobLauncher).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    @DisplayName("이미 잡이 실행 중일 경우 예외를 던진다")
    void throwsExceptionWhenJobIsAlreadyRunning() throws Exception {
        // given
        given(jobLauncher.run(any(Job.class), any(JobParameters.class)))
                .willThrow(new JobExecutionAlreadyRunningException("Job is already running"));

        // when & then
        assertThrows(RestApiException.class, () -> {
            adminScheduler.runContentBatchJob();
        });
        verify(jobLauncher).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    @DisplayName("잘못된 배치 작업 파라미터일 경우 예외를 던진다")
    void throwsExceptionForInvalidJobParameters() throws Exception {
        // given
        given(jobLauncher.run(any(Job.class), any(JobParameters.class)))
                .willThrow(new JobParametersInvalidException("Invalid job parameters"));

        // when & then
        assertThrows(RestApiException.class, () -> {
            adminScheduler.runContentBatchJob();
        });
        verify(jobLauncher).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    @DisplayName("루씬 인덱스 리빌드 재시도 메서드가 정상 작동한다")
    void rebuildLuceneIndexWithRetry_Success() {
        // given
        doNothing().when(luceneIndexService).buildIndexOnStartup();

        // when
        adminScheduler.rebuildLuceneIndexWithRetry();

        // then
        verify(luceneIndexService).buildIndexOnStartup();
    }
}