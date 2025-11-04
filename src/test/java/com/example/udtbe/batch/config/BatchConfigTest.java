package com.example.udtbe.batch.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.AdminContentDeleteJobFixture;
import com.example.udtbe.common.fixture.AdminContentRegisterJobFixture;
import com.example.udtbe.common.fixture.AdminContentUpdateJobFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        scripts = "classpath:data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@SpringBatchTest
class BatchConfigTest extends ApiSupport {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AdminContentRegisterJobRepository adminContentRegisterJobRepository;

    @Autowired
    private AdminContentUpdateJobRepository adminContentUpdateJobRepository;

    @Autowired
    private AdminContentDeleteJobRepository adminContentDeleteJobRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentMetadataRepository contentMetadataRepository;

    @BeforeEach
    void setUp() {
        adminContentRegisterJobRepository.deleteAllInBatch();
        adminContentUpdateJobRepository.deleteAllInBatch();
        adminContentDeleteJobRepository.deleteAllInBatch();
        contentMetadataRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
    }

    @DisplayName("contentRegisterStep을 성공할 수 있다.")
    @Test
    void contentRegisterStep() {
        // given
        AdminContentRegisterJob pendingJob = AdminContentRegisterJobFixture.createPendingJob(1L,
                "체인소 맨", "레제");
        adminContentRegisterJobRepository.save(pendingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", "all")
                .addString("date", LocalDateTime.now().toString())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("contentRegisterStep",
                jobParameters);

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        AdminContentRegisterJob completedJob = adminContentRegisterJobRepository.findAll().get(0);
        assertThat(completedJob.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        long contentCount = contentRepository.count();
        assertThat(contentCount).isEqualTo(1);
    }

    @DisplayName("contentUpdateStep을 성공할 수 있다.")
    @Test
    void contentUpdateStep() {
        // given
        Content content = ContentFixture.content("체인소 맨", "레제");
        ContentMetadata contentMetadata = ContentMetadataFixture.dramaMetadata(content);

        contentRepository.save(content);
        contentMetadataRepository.save(contentMetadata);

        String updatedTitle = "체인소 맨 극장판";
        String updatedDesc = "지배의 악마";

        AdminContentUpdateJob pendingJob = AdminContentUpdateJobFixture.createPendingJob(1L,
                content.getId(), updatedTitle, updatedDesc);
        adminContentUpdateJobRepository.save(pendingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", "all")
                .addString("date", LocalDateTime.now().toString())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("contentUpdateStep",
                jobParameters);

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        AdminContentUpdateJob completedJob = adminContentUpdateJobRepository.findAll().get(0);
        assertThat(completedJob.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Content updatedContent = contentRepository.findById(content.getId()).get();
        assertThat(updatedContent.getTitle()).isEqualTo(updatedTitle);
        assertThat(updatedContent.getDescription()).isEqualTo(updatedDesc);
    }

    @DisplayName("contentUpdateStep에서 예외 발생 시 스킵 처리될 수 있다.")
    @Test
    void contentUpdateStepWhenExceptionThenSkipHandled() throws Exception {

        Content content = ContentFixture.content("체인소 맨", "레제");
        ContentMetadata contentMetadata = ContentMetadataFixture.dramaMetadata(content);
        contentRepository.save(content);
        contentMetadataRepository.save(contentMetadata);

        // given
        AdminContentUpdateJob pendingJob = AdminContentUpdateJobFixture.createPendingJob(
                1L,
                100L,
                "체인소 맨 극장판",
                "마키마 씨는 어떤 맛일까?"
        );
        adminContentUpdateJobRepository.save(pendingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", "all")
                .addString("date", LocalDateTime.now().toString())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("contentUpdateStep",
                jobParameters);

        assertThat(jobExecution.getExitStatus().getExitCode())
                .isIn(ExitStatus.COMPLETED.getExitCode(), ExitStatus.FAILED.getExitCode());

        AdminContentUpdateJob failedJob = adminContentUpdateJobRepository.findAll().get(0);
        assertThat(failedJob.getStatus()).isEqualTo(BatchStatus.INVALID);

        Content unUpdatedContent = contentRepository.findById(content.getId()).get();
        assertThat(unUpdatedContent.getTitle()).isEqualTo(content.getTitle());
    }

    @DisplayName("contentDeleteStep을 성공할 수 있다.")
    @Test
    void contentDeleteStep() {
        // given
        Content content = ContentFixture.content("체인소 맨", "덴지");
        ContentMetadata contentMetadata = ContentMetadataFixture.deletedMetadata(content);
        contentRepository.save(content);
        contentMetadataRepository.save(contentMetadata);

        AdminContentDeleteJob pendingJob = AdminContentDeleteJobFixture.createPendingJob(1L,
                content.getId());
        adminContentDeleteJobRepository.save(pendingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", "all")
                .addString("date", LocalDateTime.now().toString())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("contentDeleteStep",
                jobParameters);

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        AdminContentDeleteJob completedJob = adminContentDeleteJobRepository.findAll().get(0);
        assertThat(completedJob.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Content deletedContent = contentRepository.findById(content.getId()).get();
        assertThat(deletedContent.isDeleted()).isEqualTo(true);
    }

}
