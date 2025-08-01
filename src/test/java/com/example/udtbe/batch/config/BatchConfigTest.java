package com.example.udtbe.batch.config;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.AdminContentDeleteJobFixture;
import com.example.udtbe.common.fixture.AdminContentRegisterJobFixture;
import com.example.udtbe.common.fixture.AdminContentUpdateJobFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.MemberFixture;
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
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SpringBatchTest
@Sql(scripts = "classpath:data-test.sql")
@Sql(scripts = "classpath:batch-test.sql")
class BatchConfigTest extends ApiSupport {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AdminContentUpdateJobRepository adminContentUpdateJobRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ContentMetadataRepository contentMetadataRepository;
    @Autowired
    private AdminContentDeleteJobRepository adminContentDeleteJobRepository;
    @Autowired
    private AdminContentRegisterJobRepository adminContentRegisterJobRepository;

    @AfterEach
    void tearDown() {
        adminContentUpdateJobRepository.deleteAllInBatch();
        adminContentDeleteJobRepository.deleteAllInBatch();
        adminContentRegisterJobRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("content Job들을 수행할 수 있다.")
    void contentUpdateRequestJobSuccess() throws Exception {
        // given
        Member member = MemberFixture.member("String@naber.com", ROLE_ADMIN);
        memberRepository.save(member);

        Content updateContent1 = ContentFixture.content("tit;e", "dis");
        contentRepository.save(updateContent1);

        ContentMetadata updateContentMetadata1 = ContentMetadataFixture.dramaMetadata(
                updateContent1);
        contentMetadataRepository.save(updateContentMetadata1);

        Content delContent1 = ContentFixture.content("del", "delete");
        contentRepository.save(delContent1);

        Content delContent2 = ContentFixture.content("del", "delete");
        contentRepository.save(delContent2);

        ContentMetadata delContentMetadata1 = ContentMetadataFixture.dramaMetadata(delContent1);
        contentMetadataRepository.save(delContentMetadata1);

        ContentMetadata delContentMetadata2 = ContentMetadataFixture.dramaMetadata(delContent2);
        contentMetadataRepository.save(delContentMetadata2);

        AdminContentUpdateJob updateJob1 = AdminContentUpdateJobFixture.createPendingJob(
                member.getId(), updateContent1.getId(),
                "title_job1", "설명1");
        AdminContentUpdateJob updateJob2 = AdminContentUpdateJobFixture.createPendingJob(
                member.getId(), updateContent1.getId(),
                "title_job2", "설명2");

        adminContentUpdateJobRepository.saveAll(List.of(updateJob1, updateJob2));
        adminContentDeleteJobRepository.save(
                AdminContentDeleteJobFixture.createPendingJob(member.getId(), delContent1.getId()));
        adminContentDeleteJobRepository.save(
                AdminContentDeleteJobFixture.createPendingJob(member.getId(), delContent2.getId()));

        adminContentRegisterJobRepository.save(
                AdminContentRegisterJobFixture.createPendingJob(member.getId(), "추가 콘텐츠1", "타이틀"));
        adminContentRegisterJobRepository.save(
                AdminContentRegisterJobFixture.createPendingJob(member.getId(), "추가 콘텐츠2", "타이틀"));

        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // update 검증
        List<AdminContentUpdateJob> jobs = adminContentUpdateJobRepository.findAll();
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getStatus().name()).isEqualTo(BatchStatus.COMPLETED.name());
        assertThat(jobs.get(1).getStatus().name()).isEqualTo(BatchStatus.COMPLETED.name());

        Content updatedContent = contentRepository.findById(updateContent1.getId()).get();
        assertThat(updatedContent.getTitle()).isEqualTo(updateJob2.getTitle());

        updateContentMetadata1 = contentMetadataRepository.findById(updateContentMetadata1.getId())
                .get();
        assertThat(updateContentMetadata1.getTitle()).isEqualTo(updateJob2.getTitle());

        // delete 검증
        List<AdminContentDeleteJob> deleteJobs = adminContentDeleteJobRepository.findAll();
        assertThat(deleteJobs).hasSize(2);
        assertThat(deleteJobs.get(0).getStatus().name()).isEqualTo(BatchStatus.COMPLETED.name());
        assertThat(deleteJobs.get(1).getStatus().name()).isEqualTo(BatchStatus.COMPLETED.name());

        delContent1 = contentRepository.findById(delContent1.getId()).get();
        assertThat(delContent1.isDeleted()).isEqualTo(true);

        delContent2 = contentRepository.findById(delContent1.getId()).get();
        assertThat(delContent2.isDeleted()).isEqualTo(true);

        delContentMetadata1 = contentMetadataRepository.findById(delContentMetadata1.getId()).get();
        assertThat(delContentMetadata1.isDeleted()).isEqualTo(true);

        delContentMetadata2 = contentMetadataRepository.findById(delContentMetadata2.getId()).get();
        assertThat(delContentMetadata2.isDeleted()).isEqualTo(true);

        List<AdminContentRegisterJob> registerJobs = adminContentRegisterJobRepository.findAll();
        assertThat(registerJobs).hasSize(2);
        assertThat(registerJobs.get(0).getStatus().name()).isEqualTo(BatchStatus.COMPLETED.name());
        assertThat(registerJobs.get(1).getStatus().name()).isEqualTo(BatchStatus.COMPLETED.name());
    }
}